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
package com.db4o.cs.internal;

import com.db4o.foundation.*;
import com.db4o.internal.*;

public class ClientTransactionHandle {
	
	
    private final ClientTransactionPool _transactionPool;
    private Transaction _mainTransaction;
    private Transaction _transaction;
    private boolean _rollbackOnClose;
    
    private Tree _prefetchedIDs;
	
    public ClientTransactionHandle(ClientTransactionPool transactionPool) {
		_transactionPool = transactionPool;
        _mainTransaction = _transactionPool.acquireMain();
		_rollbackOnClose = true;
	}

    public void acquireTransactionForFile(String fileName) {
    	cleanUpCurrentTransaction();
        _transaction = _transactionPool.acquire(fileName);
	}
	
    public void releaseTransaction(ShutdownMode mode) {
		if (_transaction != null) {
			cleanUpCurrentTransaction();
			_transactionPool.release(mode, _transaction, _rollbackOnClose);
			_transaction = null;
		}
	}
	
    public boolean isClosed() {
		return _transactionPool.isClosed();
	}
    
    public void close(ShutdownMode mode) {
		if ((!_transactionPool.isClosed()) && (_mainTransaction != null)) {
			_transactionPool.release(mode, _mainTransaction, _rollbackOnClose);
        }
	}
	
    public Transaction transaction() {
        if (_transaction != null) {
            return _transaction;
        }
        return _mainTransaction;
    }

    public void transaction(Transaction transaction) {
    	cleanUpCurrentTransaction();
		if (_transaction != null) {
			_transaction = transaction;
		} else {
			_mainTransaction = transaction;
		}
		_rollbackOnClose = false;
    }
    
    
	private void cleanUpCurrentTransaction() {
		freePrefetchedIDs();
	}

	public int prefetchID() {
		int id = container().getPointerSlot();
	    _prefetchedIDs = Tree.add(_prefetchedIDs, new TreeInt(id));
	    return id;
	}

	private LocalObjectContainer container() {
		return ((LocalObjectContainer)transaction().container());
	}

	public void prefetchedIDConsumed(int id) {
        _prefetchedIDs = _prefetchedIDs.removeLike(new TreeIntObject(id));
	}
	
    final void freePrefetchedIDs() {
        if (_prefetchedIDs == null) {
        	return;
        }
    	final LocalObjectContainer container = container();
        _prefetchedIDs.traverse(new Visitor4() {
            public void visit(Object node) {
            	TreeInt intNode = (TreeInt) node;
            	container.free(intNode._key, Const4.POINTER_LENGTH);
            }
        });
        _prefetchedIDs = null;
    }

}
