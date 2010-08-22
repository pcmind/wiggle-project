/* This file is part of the db4o object database http://www.db4o.com

Copyright (C) 2004 - 2009  Versant Corporation http://www.versant.com

db4o is free software; you can redistribute it and/or modify it under
the terms of version 3 of the GNU General Public License as published
by the Free Software Foundation.

db4o is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program.  If not, see http://www.gnu.org/licenses/. */
package com.db4o.internal.btree;

import com.db4o.*;
import com.db4o.defragment.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.marshall.*;

/**
 * @exclude
 */
public class BTree extends PersistentBase implements TransactionParticipant, BTreeStructureListener {
    
    private static final byte BTREE_VERSION = (byte)1;
    
    private static final int DEFRAGMENT_INCREMENT_OFFSET = 
    	1  // version byte
    + Const4.INT_LENGTH * 2;  // size, node size  
    
    private final Indexable4 _keyHandler;
    
    private BTreeNode _root;
   
    /**
     * All instantiated nodes are held in this tree. 
     */
    private TreeIntObject _nodes;
    
    private int _size;
    
    private Visitor4 _removeListener;
    
    private final TransactionLocal<Integer> _sizeDeltaInTransaction = new TransactionLocal<Integer>() {
    	@Override public Integer initialValueFor(Transaction transaction) {
    		return 0;
    	}
    };
    
    protected Queue4 _processing;
    
    private int _nodeSize;
    
    int _halfNodeSize;
    
    private BTreeStructureListener _structureListener;
    
	private boolean _disposed;
    
    public BTree(Transaction trans, Indexable4 keyHandler) {
    	this(trans, 0, keyHandler);
    }
    
    public BTree(Transaction trans, int id, Indexable4 keyHandler){
		this(trans, id, keyHandler, config(trans).bTreeNodeSize());
    }	

	public BTree(Transaction trans, int id, Indexable4 keyHandler, final int treeNodeSize) {
		if (null == keyHandler) {
    		throw new ArgumentNullException();
    	}
		_nodeSize = treeNodeSize;
		
        _halfNodeSize = _nodeSize / 2;
        _nodeSize = _halfNodeSize * 2;
        _keyHandler = keyHandler;
        if(id == 0){
            setStateDirty();
            _root = new BTreeNode(this, 0, true, 0, 0, 0);
            _root.write(trans.systemTransaction());
            addNode(_root);
            write(trans.systemTransaction());
        }else{
            setID(id);
            setStateDeactivated();
        }
	}
    
	public BTreeNode root() {
		return _root;
	}
    
    public int nodeSize() {
		return _nodeSize;
	}
    
    public void add(Transaction trans, Object key){
    	keyCantBeNull(key);
    	PreparedComparison preparedComparison = _keyHandler.prepareComparison(trans.context(), key);
    	add(trans, preparedComparison, key);
    }

	public void add(Transaction trans, PreparedComparison preparedComparison, Object key){	
        ensureDirty(trans);
        BTreeNode rootOrSplit = _root.add(trans, preparedComparison, key);
        if(rootOrSplit != null && rootOrSplit != _root){
            _root = new BTreeNode(trans, _root, rootOrSplit);
            _root.write(trans.systemTransaction());
            addNode(_root);
        }
    }

    // FIXME: Change the signature to return true, if object could be removed.
	public void remove(Transaction trans, Object key){
    	keyCantBeNull(key);
    	
    	PreparedComparison preparedComparison = keyHandler().prepareComparison(trans.context(), key);
    	
        final Iterator4 pointers = search(trans, preparedComparison).pointers();
        if (!pointers.moveNext()) {
        	return;
        }
        BTreePointer first = (BTreePointer)pointers.current();
        ensureDirty(trans);
        BTreeNode node = first.node();
        node.remove(trans, preparedComparison, key, first.index());
    }
    
    public BTreeRange search(Transaction trans, Object key) {
    	keyCantBeNull(key);
    	return search(trans, keyHandler().prepareComparison(trans.context(), key));
    }
    
    private BTreeRange search(Transaction trans, PreparedComparison preparedComparison) {
    	ensureActive(trans);
        
        // TODO: Optimize the following.
        //       Part of the search operates against the same nodes.
        //       As long as the bounds are on one node, the search
        //       should walk the nodes in one go.
        
        BTreeNodeSearchResult start = searchLeaf(trans, preparedComparison, SearchTarget.LOWEST);
        BTreeNodeSearchResult end = searchLeaf(trans, preparedComparison, SearchTarget.HIGHEST);
        return start.createIncludingRange(end);
    }
    
    private void keyCantBeNull(Object key) {
    	if (null == key) {
    		throw new ArgumentNullException();
    	}
	}
    
    public Indexable4 keyHandler() {
    	return _keyHandler;
    }

	public BTreeNodeSearchResult searchLeaf(Transaction trans, Object key, SearchTarget target) {
	    return searchLeaf(trans, _keyHandler.prepareComparison(trans.context(), key), target);
    }
	
	public BTreeNodeSearchResult searchLeaf(Transaction trans, PreparedComparison preparedComparison, SearchTarget target) {
        ensureActive(trans);
        return _root.searchLeaf(trans, preparedComparison, target);
    }
    
    public void commit(final Transaction transaction){
    	if(_disposed){
    		return;
    	}
    	updateSize(transaction);
    	commitNodes(transaction);
    	finishTransaction(transaction);
    }

	private void updateSize(final Transaction transaction) {
	    final ByRef<Integer> sizeInTransaction = sizeIn(transaction);
		_size += sizeInTransaction.value;
		sizeInTransaction.value = 0;
    }

	private ByRef<Integer> sizeIn(final Transaction trans) {
	    return trans.get(_sizeDeltaInTransaction);
    }
    
    public void commitNodes(final Transaction trans){
        processEachNode(new Procedure4<BTreeNode>() { public void apply(BTreeNode node) {
			node.commit(trans);
		}});
    }
    
    private void processEachNode(Procedure4<BTreeNode> action) {
        if(_nodes == null)
        	return;
        processAllNodes();
        while(_processing.hasNext()){
        	action.apply((BTreeNode)_processing.next());
        }
        _processing = null;
    }
    
    public void rollback(final Transaction trans){
        rollbackNodes(trans);
        finishTransaction(trans);
    }

	private void finishTransaction(final Transaction trans) {
	    final Transaction systemTransaction = trans.systemTransaction();
        writeAllNodes(systemTransaction);
        setStateDirty();
        write(systemTransaction);
        purge();
    }

	private void rollbackNodes(final Transaction trans) {
	    processEachNode(new Procedure4<BTreeNode>() { public void apply(BTreeNode node) {
            node.rollback(trans);
        }});
    }
    
    private void writeAllNodes(final Transaction systemTransaction){
        if(_nodes == null){
        	return;
        }
    	_nodes.traverse(new Visitor4<TreeIntObject>() {
            public void visit(TreeIntObject obj) {
                BTreeNode node = (BTreeNode)obj.getObject();
                node.write(systemTransaction);
            }
        });
    }
    
    
    private void purge(){
        if(_nodes == null){
            return;
        }
        
        Tree temp = _nodes;
        _nodes = null;
        
        // FIXME: Caching doesn't work as expected. Disabled.
        //        Reimplement with LRU cache for all nodes,
        //        independant of _root.
        
//        if(_cacheHeight > 0){
//            _root.markAsCached(_cacheHeight);
//        }else{
        
            _root.holdChildrenAsIDs();
            addNode(_root);
            
//        }
        
        temp.traverse(new Visitor4() {
            public void visit(Object obj) {
                BTreeNode node = (BTreeNode)((TreeIntObject)obj).getObject();
                node.purge();
            }
        });
    }
    
    private void processAllNodes(){
        _processing = new NonblockingQueue();
        _nodes.traverse(new Visitor4<TreeIntObject>() {
            public void visit(TreeIntObject node) {
                _processing.add(node.getObject());
            }
        });
    }
    
    private void ensureActive(Transaction trans){
        if(! isActive()){
            read(trans.systemTransaction());
        }
    }
    
    private void ensureDirty(Transaction trans){
        ensureActive(trans);
        if(canEnlistWithTransaction()){
        	((LocalTransaction)trans).enlist(this);	
        }
        setStateDirty();
    }
    
    protected boolean canEnlistWithTransaction(){
    	return true;
    }
    
    public byte getIdentifier() {
        return Const4.BTREE;
    }
    
    public void setRemoveListener(Visitor4 vis){
        _removeListener = vis;
    }
    
    public int ownLength() {
        return 1 + Const4.OBJECT_LENGTH + (Const4.INT_LENGTH * 2) + Const4.ID_LENGTH;
    }
    
    public BTreeNode produceNode(int id){
        TreeIntObject addtio = new TreeIntObject(id);
        _nodes = (TreeIntObject)Tree.add(_nodes, addtio);
        TreeIntObject tio = (TreeIntObject)addtio.addedOrExisting();
        BTreeNode node = (BTreeNode)tio.getObject();
        if(node == null){
            node = new BTreeNode(id, this);
            tio.setObject(node);
            addToProcessing(node);
        }
        return node;
    }
    
    void addNode(BTreeNode node){
        _nodes = (TreeIntObject)Tree.add(_nodes, new TreeIntObject(node.getID(), node));
        addToProcessing(node);
    }
    
    void addToProcessing(BTreeNode node){
        if(_processing != null){
            _processing.add(node);
        }
    }
    
    void removeNode(BTreeNode node){
        _nodes = (TreeIntObject)_nodes.removeLike(new TreeInt(node.getID()));
    }
    
    void notifyRemoveListener(Object obj){
        if(_removeListener != null){
            _removeListener.visit(obj);
        }
    }

    public void readThis(Transaction a_trans, ByteArrayBuffer a_reader) {
        a_reader.incrementOffset(1);  // first byte is version, for possible future format changes
        _size = a_reader.readInt();
        _nodeSize = a_reader.readInt();
        _halfNodeSize = nodeSize() / 2;
        _root = produceNode(a_reader.readInt());
    }
    
    public void writeThis(Transaction trans, ByteArrayBuffer a_writer) {
        a_writer.writeByte(BTREE_VERSION);
        a_writer.writeInt(_size);
        a_writer.writeInt(nodeSize());
        a_writer.writeIDOf(trans, _root);
    }
    
    public int size(Transaction trans){
    	
		// This implementation of size will not work accurately for multiple
		// transactions. If two transactions call clear and both commit, _size
		// can end up negative.
		
		// For multiple transactions the size patches only are an estimate.
    	
        ensureActive(trans);
        return _size + sizeIn(trans).value;
    }
    
    public void traverseKeys(Transaction trans, Visitor4 visitor){
        ensureActive(trans);
        if(_root == null){
            return;
        }
        _root.traverseKeys(trans, visitor);
    }
    
    public void sizeChanged(Transaction transaction, BTreeNode node, int changeBy){
    	notifyCountChanged(transaction, node, changeBy);

    	final ByRef<Integer> sizeInTransaction = sizeIn(transaction);
		sizeInTransaction.value = sizeInTransaction.value + changeBy;
		
    }

	public void dispose(Transaction transaction) {
	}

	public BTreePointer firstPointer(Transaction trans) {
		ensureActive(trans);
		if (null == _root) {
			return null;
		}
		return _root.firstPointer(trans);
	}
	
	public BTreePointer lastPointer(Transaction trans) {
		ensureActive(trans);
		if (null == _root) {
			return null;
		}
		return _root.lastPointer(trans);
	}
	
	public BTree debugLoadFully(Transaction trans) {
		ensureActive(trans);
		_root.debugLoadFully(trans);
		return this;
	}
    
    private void traverseAllNodes(Transaction trans, Visitor4 command) {
        ensureActive(trans);
        _root.traverseAllNodes(trans, command);
    }

	public void defragIndex(DefragmentContextImpl context) {
        if (Deploy.debug) {
            context.readBegin(Const4.BTREE);
        }
		context.incrementOffset(DEFRAGMENT_INCREMENT_OFFSET);
		context.copyID();
        if (Deploy.debug) {
            context.readEnd();
        }
	}

	public void defragIndexNode(DefragmentContextImpl context) {
		BTreeNode.defragIndex(context, _keyHandler);
	}

	public void defragBTree(final DefragmentServices services) {
		DefragmentContextImpl.processCopy(services,getID(),new SlotCopyHandler() {
			public void processCopy(DefragmentContextImpl context) {
				defragIndex(context);
			}
		});
		services.traverseAllIndexSlots(this, new Visitor4() {
			public void visit(Object obj) {
				final int id=((Integer)obj).intValue();
					DefragmentContextImpl.processCopy(services, id, new SlotCopyHandler() {
						public void processCopy(DefragmentContextImpl context) {
							defragIndexNode(context);
						}
					});
			}
		});
	}

	public int compareKeys(Context context, Object key1, Object key2) {
		PreparedComparison preparedComparison = _keyHandler.prepareComparison(context, key1);
		return preparedComparison.compareTo(key2);
	}
	
	private static Config4Impl config(Transaction trans) {
		if (null == trans) {
			throw new ArgumentNullException();
		}
		return trans.container().configImpl();
	}

    public void free(Transaction systemTrans) {
    	_disposed = true;
        freeAllNodeIds(systemTrans, allNodeIds(systemTrans));
        super.free(systemTrans);
    }

	private void freeAllNodeIds(Transaction systemTrans, final Iterator4 allNodeIDs) {
        while(allNodeIDs.moveNext()){
            int id = ((Integer)allNodeIDs.current()).intValue();
            systemTrans.slotFreePointerOnCommit(id);
        }
	}

	public Iterator4 allNodeIds(Transaction systemTrans) {
		final Collection4 allNodeIDs = new Collection4(); 
        traverseAllNodes(systemTrans, new Visitor4() {
            public void visit(Object node) {
                allNodeIDs.add(new Integer(((BTreeNode)node).getID()));
            }
        });
		return allNodeIDs.iterator();
	}
	
	public BTreeRange asRange(Transaction trans){
		return new BTreeRangeSingle(trans, this, firstPointer(trans), null);
	}
	
    private void traverseAllNodes(final Visitor4 visitor){
    	if(_nodes == null){
    		return;
    	}
    	_nodes.traverse(new Visitor4() {
			public void visit(Object obj) {
				visitor.visit(((TreeIntObject)obj).getObject());
			}
		});
    }
    
    public String toString() {
    	final StringBuffer sb = new StringBuffer();
    	sb.append("BTree ");
    	sb.append(getID());
    	sb.append(" Active Nodes: \n");
    	traverseAllNodes(new Visitor4() {
			public void visit(Object obj) {
				sb.append(obj);
				sb.append("\n");
			}
		});
    	return sb.toString();
    }

	public void structureListener(BTreeStructureListener listener) {
		_structureListener = listener;
	}

	public void notifySplit(Transaction trans, BTreeNode originalNode, BTreeNode newRightNode) {
		if(_structureListener != null){
			_structureListener.notifySplit(trans, originalNode, newRightNode);
		}
	}

	public void notifyDeleted(Transaction trans, BTreeNode node) {
		if(_structureListener != null){
			_structureListener.notifyDeleted(trans, node);
		}
	}

	public void notifyCountChanged(Transaction trans, BTreeNode node, int diff) {
		if(_structureListener != null){
			_structureListener.notifyCountChanged(trans, node, diff);
		}
	}

	public Iterator4 iterator(Transaction trans) {
		return new BTreeIterator(trans, this);
	}

	public void clear(Transaction transaction) {
		BTreePointer currentPointer = firstPointer(transaction);
		while(currentPointer != null && currentPointer.isValid()){
			BTreeNode node = currentPointer.node();
			int index = currentPointer.index();
			node.remove(transaction, index);
			currentPointer = currentPointer.next();
		}
	}
    
}

