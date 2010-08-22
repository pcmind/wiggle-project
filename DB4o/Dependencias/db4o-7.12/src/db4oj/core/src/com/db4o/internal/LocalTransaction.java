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
package com.db4o.internal;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.activation.*;
import com.db4o.internal.caching.*;
import com.db4o.internal.callbacks.*;
import com.db4o.internal.freespace.*;
import com.db4o.internal.references.*;
import com.db4o.internal.slots.*;
import com.db4o.internal.transactionlog.*;

/**
 * @exclude
 */
public class LocalTransaction extends Transaction {

	private final byte[] _pointerBuffer = new byte[Const4.POINTER_LENGTH];
    
    protected final StatefulBuffer i_pointerIo;    
    
    private final IdentitySet4 _participants = new IdentitySet4(); 

    private final LockedTree _slotChanges = new LockedTree();
	
    Tree _writtenUpdateAdjustedIndexes;
    
	protected final LocalObjectContainer _file;
	
	private final CommittedCallbackDispatcher _committedCallbackDispatcher;
	
	private final Cache4<Integer, ByteArrayBuffer> _slotCache;
	
	private TransactionLogHandler _transactionLogHandler = new EmbeddedTransactionLogHandler();

	public LocalTransaction(ObjectContainerBase container, Transaction parentTransaction, ReferenceSystem referenceSystem) {
		super(container, parentTransaction, referenceSystem);
		_file = (LocalObjectContainer) container;
        i_pointerIo = new StatefulBuffer(this, Const4.POINTER_LENGTH);
        
        _committedCallbackDispatcher = new CommittedCallbackDispatcher() {
    		public boolean willDispatchCommitted() {
    			return callbacks().caresAboutCommitted();
    		}
    		public void dispatchCommitted(CallbackObjectInfoCollections committedInfo) {
    			callbacks().commitOnCompleted(LocalTransaction.this, committedInfo, false);
    		}
    	};
    	_slotCache = createSlotCache();
    	initializeTransactionLogHandler();
	}

	private Cache4<Integer, ByteArrayBuffer> createSlotCache() {
	    if(isSystemTransaction()) {
	    	int slotCacheSize = config().slotCacheSize();
	    	if (slotCacheSize > 0) {
	    		return CacheFactory.new2QCache(slotCacheSize);
	    	}
    	}
    	return new NullCache4<Integer, ByteArrayBuffer>();
    }
	
	private void initializeTransactionLogHandler() {
    	if(! isSystemTransaction()){
    		_transactionLogHandler = ((LocalTransaction)systemTransaction())._transactionLogHandler;
    		return;
    	}
		boolean fileBased = config().fileBasedTransactionLog() && container() instanceof IoAdaptedObjectContainer;
		if(! fileBased){
			_transactionLogHandler = new EmbeddedTransactionLogHandler();
			return;
		}
		String fileName = ((IoAdaptedObjectContainer)container()).fileName();
		_transactionLogHandler = new FileBasedTransactionLogHandler(this, fileName); 
	}

	public Config4Impl config() {
		return container().config();
	}

	public LocalObjectContainer file() {
		return _file;
	}
	
    public void commit() {
    	commit(_committedCallbackDispatcher);
    }
    
    public void commit(CommittedCallbackDispatcher dispatcher) {
        synchronized (container().lock()) {
        	
        	dispatchCommittingCallback();   
        	
        	if (!doCommittedCallbacks(dispatcher)) {
        		commitListeners();
        		commitImpl();
        		commitClearAll();
    		} else {
    			commitListeners();
    			Collection4 deleted = collectCommittedCallbackDeletedInfo();
                commitImpl();
                final CallbackObjectInfoCollections committedInfo = collectCommittedCallbackInfo(deleted);
        		commitClearAll();
        		dispatcher.dispatchCommitted(
        				CallbackObjectInfoCollections.EMTPY == committedInfo
        				? committedInfo
        				: new CallbackObjectInfoCollections(
        						committedInfo.added,
        						committedInfo.updated,
        						new ObjectInfoCollectionImpl(deleted)));
    		}
        }
    }	

	private void dispatchCommittingCallback() {
		if(doCommittingCallbacks()){
			callbacks().commitOnStarted(this, collectCommittingCallbackInfo());
		}
	}

	private boolean doCommittedCallbacks(CommittedCallbackDispatcher dispatcher) {
        if (isSystemTransaction()){
            return false;
        }
		return dispatcher.willDispatchCommitted();
	}

	private boolean doCommittingCallbacks() {
		if (isSystemTransaction()) {
			return false;
		}
		return callbacks().caresAboutCommitting();
	}
    
	public void enlist(TransactionParticipant participant) {
		if (null == participant) {
			throw new ArgumentNullException();
		}
		checkSynchronization();	
		if (!_participants.contains(participant)) {
			_participants.add(participant);
		}
	}

	private void commitImpl(){
        
        if(DTrace.enabled){
            DTrace.TRANS_COMMIT.logInfo( "server == " + container().isServer() + ", systemtrans == " +  isSystemTransaction());
        }
        
        commit3Stream();
        
        commitParticipants();
        
        container().writeDirty();
        
        Slot reservedSlot = _transactionLogHandler.allocateSlot(this, false);
        
        freeSlotChanges(false);
                
        freespaceBeginCommit();
        
        commitFreespace();
        
        freeSlotChanges(true);
        
        _transactionLogHandler.applySlotChanges(this, reservedSlot);
        
        freespaceEndCommit();
    }
	
	public final void freeSlotChanges(final boolean forFreespace) {
        Visitor4 visitor = new Visitor4() {
            public void visit(Object obj) {
                ((SlotChange)obj).freeDuringCommit(_file, forFreespace);
            }
        };
        if(isSystemTransaction()){
            _slotChanges.traverseMutable(visitor);
            return;
        }
        _slotChanges.traverseLocked(visitor);
        if(_systemTransaction != null){
            parentLocalTransaction().freeSlotChanges(forFreespace);
        }
    }
	
	private void commitListeners(){
        commitParentListeners(); 
        commitTransactionListeners();
    }

	private void commitParentListeners() {
		if (_systemTransaction != null) {
            parentLocalTransaction().commitListeners();
        }
	}
	
    private void commitParticipants() {
        if (parentLocalTransaction() != null) {
        	parentLocalTransaction().commitParticipants();
        }
        
        Iterator4 iterator = _participants.iterator();
		while (iterator.moveNext()) {
			((TransactionParticipant)iterator.current()).commit(this);
		}
    }
    
    private void commit3Stream(){
        container().processPendingClassUpdates();
        container().writeDirty();
        container().classCollection().write(container().systemTransaction());
    }
    
	private LocalTransaction parentLocalTransaction() {
		return (LocalTransaction) _systemTransaction;
	}
    
	private void commitClearAll(){
		if(_systemTransaction != null){
            parentLocalTransaction().commitClearAll();
        }
        clearAll();
    }

	
	protected void clear() {
		_slotChanges.clear();
		disposeParticipants();
        _participants.clear();
	}
	
	private void disposeParticipants() {
        Iterator4 iterator = _participants.valuesIterator();
		while (iterator.moveNext()) {
			((TransactionParticipant)iterator.current()).dispose(this);
		}
	}
	
    public void rollback() {
        synchronized (container().lock()) {
            
            rollbackParticipants();
            
            rollbackSlotChanges();
            
            rollBackTransactionListeners();
            
            clearAll();
        }
    }
    
    private void rollbackParticipants() {
        Iterator4 iterator = _participants.valuesIterator();
		while (iterator.moveNext()) {
			((TransactionParticipant)iterator.current()).rollback(this);
		}
	}
	
	protected void rollbackSlotChanges() {
		_slotChanges.traverseLocked(new Visitor4() {
            public void visit(Object a_object) {
                ((SlotChange) a_object).rollback(_file);
            }
        });
	}

	public boolean isDeleted(int id) {
    	return slotChangeIsFlaggedDeleted(id);
    }
	
    public void writeZeroPointer(int id){
        writePointer(id, Slot.ZERO);   
    }
    
    public void writePointer(Pointer4 pointer) {
        writePointer(pointer._id, pointer._slot);
    }

	public void writePointer(int id, Slot slot) {
        if(DTrace.enabled){
            DTrace.WRITE_POINTER.log(id);
            DTrace.WRITE_POINTER.logLength(slot);
        }
        checkSynchronization();
        i_pointerIo.useSlot(id);
        if (Deploy.debug) {
            i_pointerIo.writeBegin(Const4.YAPPOINTER);
        }
        i_pointerIo.writeInt(slot.address());
    	i_pointerIo.writeInt(slot.length());
        if (Deploy.debug) {
            i_pointerIo.writeEnd();
        }
        if (Debug4.xbytes && Deploy.overwrite) {
            i_pointerIo.setID(Const4.IGNORE_ID);
        }
        i_pointerIo.write();
    }
	
    public boolean writeSlots() {
        final BooleanByRef ret = new BooleanByRef();
        traverseSlotChanges(new Visitor4() {
			public void visit(Object obj) {
				((SlotChange)obj).writePointer(LocalTransaction.this);
				ret.value = true;
			}
		});
        return ret.value;
    }
	
    public void flushFile(){
        if(DTrace.enabled){
            DTrace.TRANS_FLUSH.log();
        }
        _file.syncFiles();
    }
    
    private SlotChange produceSlotChange(int id){
    	if(DTrace.enabled){
    		DTrace.PRODUCE_SLOT_CHANGE.log(id);
    	}
        SlotChange slot = new SlotChange(id);
        _slotChanges.add(slot);
        return (SlotChange)slot.addedOrExisting();
    }    
    
    public final SlotChange findSlotChange(int a_id) {
        checkSynchronization();
        return (SlotChange)_slotChanges.find(a_id);
    }    

    public Slot getCurrentSlotOfID(int id) {
        checkSynchronization();
        if (id == 0) {
            return null;
        }
        SlotChange change = findSlotChange(id);
        if (change != null) {
            if(change.isSetPointer()){
                return change.newSlot();
            }
        }
        
        if (_systemTransaction != null) {
            Slot parentSlot = parentLocalTransaction().getCurrentSlotOfID(id); 
            if (parentSlot != null) {
                return parentSlot;
            }
        }
        return readPointer(id)._slot;
    }
    
    public Slot getCommittedSlotOfID(int id) {
        if (id == 0) {
            return null;
        }
        SlotChange change = findSlotChange(id);
        if (change != null) {
            Slot slot = change.oldSlot();
            if(slot != null){
                return slot;
            }
        }
        
        if (_systemTransaction != null) {
            Slot parentSlot = parentLocalTransaction().getCommittedSlotOfID(id); 
            if (parentSlot != null) {
                return parentSlot;
            }
        }
		return readPointer(id)._slot;
    }

    public Pointer4 readPointer(int id) {
        if (Deploy.debug) {
            return debugReadPointer(id);
        }
        if(!isValidId(id)){
        	throw new InvalidIDException(id);
        }
        
       	_file.readBytes(_pointerBuffer, id, Const4.POINTER_LENGTH);
        int address = (_pointerBuffer[3] & 255)
            | (_pointerBuffer[2] & 255) << 8 | (_pointerBuffer[1] & 255) << 16
            | _pointerBuffer[0] << 24;
        int length = (_pointerBuffer[7] & 255)
            | (_pointerBuffer[6] & 255) << 8 | (_pointerBuffer[5] & 255) << 16
            | _pointerBuffer[4] << 24;
        
        if(!isValidSlot(address, length)){
        	throw new InvalidSlotException(address, length, id);
        }
        
        return new Pointer4(id, new Slot(address, length));
    }

	private boolean isValidId(int id) {
		return _file.fileLength() >= id;
	}

	private boolean isValidSlot(int address, int length) {
		// just in case overflow 
		long fileLength = _file.fileLength();
		
		boolean validAddress = fileLength >= address;
        boolean validLength = fileLength >= length ;
        boolean validSlot = fileLength >= (address+length);
        
        return validAddress && validLength && validSlot;
	}

	private Pointer4 debugReadPointer(int id) {
        if (Deploy.debug) {
    		i_pointerIo.useSlot(id);
    		i_pointerIo.read();
    		i_pointerIo.readBegin(Const4.YAPPOINTER);
    		int debugAddress = i_pointerIo.readInt();
    		int debugLength = i_pointerIo.readInt();
    		i_pointerIo.readEnd();
    		return new Pointer4(id, new Slot(debugAddress, debugLength));
        }
        return null;
	}
    
    public void setPointer(int a_id, Slot slot) {
        if(DTrace.enabled){
            DTrace.SLOT_SET_POINTER.log(a_id);
            DTrace.SLOT_SET_POINTER.logLength(slot);
        }
        checkSynchronization();
        produceSlotChange(a_id).setPointer(slot);
    }
    
    private boolean slotChangeIsFlaggedDeleted(int id){
        SlotChange slot = findSlotChange(id);
        if (slot != null) {
            return slot.isDeleted();
        }
        if (_systemTransaction != null) {
            return parentLocalTransaction().slotChangeIsFlaggedDeleted(id);
        }
        return false;
    }
	
	final void completeInterruptedTransaction() {
        synchronized (container().lock()) {
        	_transactionLogHandler.completeInterruptedTransaction(this);
        }
    }
	
	public void traverseSlotChanges(Visitor4 visitor){
        if(_systemTransaction != null){
        	parentLocalTransaction().traverseSlotChanges(visitor);
        }
        _slotChanges.traverseLocked(visitor);
	}
	
	public void slotDelete(int id, Slot slot) {
        checkSynchronization();
        if(DTrace.enabled){
            DTrace.SLOT_DELETE.log(id);
            DTrace.SLOT_DELETE.logLength(slot);
        }
        if (id == 0) {
            return;
        }
        SlotChange slotChange = produceSlotChange(id);
        slotChange.freeOnCommit(_file, slot);
        slotChange.setPointer(Slot.ZERO);
    }
	
    public void slotFreeOnCommit(int id, Slot slot) {
        checkSynchronization();
        if(DTrace.enabled){
            DTrace.SLOT_FREE_ON_COMMIT.log(id);
            DTrace.SLOT_FREE_ON_COMMIT.logLength(slot);
        }
        if (id == 0) {
            return;
        }
        produceSlotChange(id).freeOnCommit(_file, slot);
    }

    public void slotFreeOnRollback(int id, Slot slot) {
        checkSynchronization();
        if(DTrace.enabled){
            DTrace.SLOT_FREE_ON_ROLLBACK_ID.log(id);
            DTrace.SLOT_FREE_ON_ROLLBACK_ADDRESS.logLength(slot);
        }
        produceSlotChange(id).freeOnRollback(slot);
    }

    void slotFreeOnRollbackCommitSetPointer(int id, Slot newSlot, boolean forFreespace) {
        
        Slot oldSlot = getCurrentSlotOfID(id);
        if(oldSlot==null) {
        	return;
        }
        
        checkSynchronization();
        
        if(DTrace.enabled){
            DTrace.FREE_ON_ROLLBACK.log(id);
            DTrace.FREE_ON_ROLLBACK.logLength(newSlot);
            DTrace.FREE_ON_COMMIT.log(id);
            DTrace.FREE_ON_COMMIT.logLength(oldSlot);
        }
        
        SlotChange change = produceSlotChange(id);
        change.freeOnRollbackSetPointer(newSlot);
        change.freeOnCommit(_file, oldSlot);
        change.forFreespace(forFreespace);
    }

    void produceUpdateSlotChange(int id, Slot slot) {
        checkSynchronization();
        if(DTrace.enabled){
            DTrace.FREE_ON_ROLLBACK.log(id);
            DTrace.FREE_ON_ROLLBACK.logLength(slot);
        }
        
        final SlotChange slotChange = produceSlotChange(id);
        slotChange.freeOnRollbackSetPointer(slot);
    }
    
    public void slotFreePointerOnCommit(int a_id) {
        checkSynchronization();
        Slot slot = getCurrentSlotOfID(a_id);
        if(slot == null){
            return;
        }
        
        // FIXME: From looking at this it should call slotFreePointerOnCommit
        //        Write a test case and check.
        
        //        Looking at references, this method is only called from freed
        //        BTree nodes. Indeed it should be checked what happens here.
        
        slotFreeOnCommit(a_id, slot);
    }
    
    void slotFreePointerOnCommit(int a_id, Slot slot) {
        checkSynchronization();
        slotFreeOnCommit(slot.address(), slot);
        
        // FIXME: This does not look nice
        slotFreeOnCommit(a_id, slot);
        
        // FIXME: It should rather work like this:
        // produceSlotChange(a_id).freePointerOnCommit();
    }
    
    public void slotFreePointerOnRollback(int id) {
    	produceSlotChange(id).freePointerOnRollback();
    }
	
	public void processDeletes() {
		if (_delete == null) {
			_writtenUpdateAdjustedIndexes = null;
			return;
		}

		while (_delete != null) {

			Tree delete = _delete;
			_delete = null;

			delete.traverse(new Visitor4() {
				public void visit(Object a_object) {
					DeleteInfo info = (DeleteInfo) a_object;
					// if the object has been deleted
					if (isDeleted(info._key)) {
						return;
					}
					
					// We need to hold a hard reference here, otherwise we can get 
					// intermediate garbage collection kicking in.
					Object obj = null;  
					
					if (info._reference != null) {
						obj = info._reference.getObject();
					}
					if (obj == null || info._reference.getID() < 0) {

						// This means the object was gc'd.

						// Let's try to read it again, but this may fail in
						// CS mode if another transaction has deleted it. 

						HardObjectReference hardRef = container().getHardObjectReferenceById(
							LocalTransaction.this, info._key);
						if(hardRef == HardObjectReference.INVALID){
							return;
						}
						info._reference = hardRef._reference;
						info._reference.flagForDelete(container().topLevelCallId());
						obj = info._reference.getObject();
					}
					container().delete3(LocalTransaction.this, info._reference,
							obj, info._cascade, false);
				}
			});
		}
		_writtenUpdateAdjustedIndexes = null;
	}
	
	
	public void writeUpdateAdjustIndexes(int id, ClassMetadata clazz, ArrayType typeInfo, int cascade) {
    	new WriteUpdateProcessor(this, id, clazz, typeInfo, cascade).run();
    }
    
	private Callbacks callbacks(){
		return container().callbacks();
	}
	
	private Collection4 collectCommittedCallbackDeletedInfo() {
		final Collection4 deleted = new Collection4();
		collectSlotChanges(new SlotChangeCollector() {
			public void deleted(int id) {
				ObjectInfo ref = frozenReferenceFor(id);
				if(ref != null){
					deleted.add(ref);
				}
			}

			public void updated(int id) {
			}
		
			public void added(int id) {
			}
		});
		return deleted;
	}
	
	private CallbackObjectInfoCollections collectCommittedCallbackInfo(Collection4 deleted) {
		if (null == _slotChanges) {
			return CallbackObjectInfoCollections.EMTPY;
		}
		
		final Collection4 added = new Collection4();
		final Collection4 updated = new Collection4();		
		collectSlotChanges(new SlotChangeCollector() {
			public void added(int id) {
				added.add(lazyReferenceFor(id));
			}

			public void updated(int id) {
				updated.add(lazyReferenceFor(id));
			}
			
			public void deleted(int id) {
			}
		});
		return newCallbackObjectInfoCollections(added, updated, deleted);
	}

	private CallbackObjectInfoCollections collectCommittingCallbackInfo() {
		if (null == _slotChanges) {
			return CallbackObjectInfoCollections.EMTPY;
		}
		
		final Collection4 added = new Collection4();
		final Collection4 deleted = new Collection4();
		final Collection4 updated = new Collection4();		
		collectSlotChanges(new SlotChangeCollector() {
			public void added(int id) {
				added.add(lazyReferenceFor(id));
			}

			public void updated(int id) {
				updated.add(lazyReferenceFor(id));
			}
			
			public void deleted(int id){
				ObjectInfo ref = frozenReferenceFor(id);
				if(ref != null){
					deleted.add(ref);
				}
			}
		});
		return newCallbackObjectInfoCollections(added, updated, deleted);
	}

	private CallbackObjectInfoCollections newCallbackObjectInfoCollections(
			final Collection4 added,
			final Collection4 updated,
			final Collection4 deleted) {
		return new CallbackObjectInfoCollections(
				new ObjectInfoCollectionImpl(added),
				new ObjectInfoCollectionImpl(updated),
				new ObjectInfoCollectionImpl(deleted));
	}

	private void collectSlotChanges(final SlotChangeCollector collector) {
		if (null == _slotChanges) {
			return;
		}
		_slotChanges.traverseLocked(new Visitor4() {
			public void visit(Object obj) {
				final SlotChange slotChange = ((SlotChange)obj);
				final int id = slotChange._key;
				if (slotChange.isDeleted()) {
					if(! slotChange.isNew()){
						collector.deleted(id);
					}
				} else if (slotChange.isNew()) {
					collector.added(id);
				} else {
					collector.updated(id);
				}
			}
		});
	}
	
	public ObjectInfo frozenReferenceFor(final int id) {
		ObjectReference ref = referenceForId(id);
		if(ref != null){
			return new FrozenObjectInfo(this, ref, true);
		}
		ref = container().peekReference(systemTransaction(), id, new FixedActivationDepth(0), true);
		if(ref == null || ref.getObject() == null){
			return null;
		}
		return new FrozenObjectInfo(systemTransaction(), ref, true);
	}
	
	public static Transaction readInterruptedTransaction(LocalObjectContainer file, ByteArrayBuffer reader) {
		LocalTransaction transaction = (LocalTransaction) file.newTransaction(null, null);
		if(transaction.wasInterrupted(reader)){
			return transaction;
		}
	    return null;
	}
	
	public boolean wasInterrupted(ByteArrayBuffer reader){
		return _transactionLogHandler.checkForInterruptedTransaction(this, reader);
	}
	
	public FreespaceManager freespaceManager(){
		return _file.freespaceManager();
	}
	
    private void freespaceBeginCommit(){
        if(freespaceManager() == null){
            return;
        }
        freespaceManager().beginCommit();
    }
    
    private void freespaceEndCommit(){
        if(freespaceManager() == null){
            return;
        }
        freespaceManager().endCommit();
    }
    
    private void commitFreespace(){
        if(freespaceManager() == null){
            return;
        }
        freespaceManager().commit();
    }

	public LazyObjectReference lazyReferenceFor(final int id) {
		return new LazyObjectReference(LocalTransaction.this, id);
	}
	
	public Cache4<Integer, ByteArrayBuffer> slotCache(){
		return _slotCache;
	}

	public void readSlotChanges(ByteArrayBuffer buffer) {
		_slotChanges.read(buffer, new SlotChange(0));
	}

	public void close() {
		_transactionLogHandler.close();
		discardReferenceSystem();
	}
	
}
