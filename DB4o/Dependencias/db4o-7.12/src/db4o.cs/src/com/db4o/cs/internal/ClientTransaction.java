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

import com.db4o.cs.internal.messages.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.references.*;

public final class ClientTransaction extends Transaction {

    private final ClientObjectContainer _client;
    
    protected Tree _objectRefrencesToGC;
    
    ClientTransaction(ClientObjectContainer container, Transaction parentTransaction, ReferenceSystem referenceSystem) {
        super(container, parentTransaction, referenceSystem);
        _client = container;
    }
    
    public void commit() {
    	commitTransactionListeners();
        clearAll();
        if(isSystemTransaction()){
        	_client.write(Msg.COMMIT_SYSTEMTRANS);
        }else{
        	_client.write(Msg.COMMIT);
        	_client.expectedResponse(Msg.OK);
        }
    }
    
    protected void clear() {
    	removeObjectReferences();
    }

	private void removeObjectReferences() {
		if(_objectRefrencesToGC != null){
            _objectRefrencesToGC.traverse(new Visitor4() {
                public void visit(Object a_object) {
                    ObjectReference yo = (ObjectReference)((TreeIntObject) a_object)._object;
                    ClientTransaction.this.removeReference(yo);
                }
            });
        }
        _objectRefrencesToGC = null;
	}

    public boolean delete(ObjectReference ref, int id, int cascade) {
        if (! super.delete(ref, id, cascade)){
        	return false;
        }
        MsgD msg = Msg.TA_DELETE.getWriterForInts(this, new int[] {id, cascade});
        _client.writeBatchedMessage(msg);
        return true;
    }

    public boolean isDeleted(int a_id) {

        // This one really is a hack.
        // It only helps to get information about the current
        // transaction.

        // We need a better strategy for C/S concurrency behaviour.
        MsgD msg = Msg.TA_IS_DELETED.getWriterForInt(this, a_id);
		_client.write(msg);
        int res = _client.expectedByteResponse(Msg.TA_IS_DELETED).readInt();
        return res == 1;
    }
    
    public void processDeletes() {
        Visitor4 deleteVisitor = new Visitor4() {
            public void visit(Object a_object) {
                DeleteInfo info = (DeleteInfo) a_object;
                if (info._reference != null) {
                    _objectRefrencesToGC = Tree.add(_objectRefrencesToGC, new TreeIntObject(info._key, info._reference));
                }
            }
        };
        traverseDelete(deleteVisitor);
		_client.writeBatchedMessage(Msg.PROCESS_DELETES);
    }

    public void rollback() {
        _objectRefrencesToGC = null;
        rollBackTransactionListeners();
        clearAll();
    }

    public void writeUpdateAdjustIndexes(int id, ClassMetadata classMetadata, ArrayType arrayType,
        int cascade) {
    	// do nothing
    }

}