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
import com.db4o.foundation.*;
import com.db4o.internal.delete.*;
import com.db4o.internal.marshall.*;
import com.db4o.internal.slots.*;

/**
 * @exclude
 */
class WriteUpdateProcessor {
	
	private final LocalTransaction _localTransaction;

	private final int _id;
	
	private final ClassMetadata _clazz;
	
	private final ArrayType _typeInfo;
	
	private int _cascade;

	public WriteUpdateProcessor(LocalTransaction localTransaction, 
			int id, 
			ClassMetadata clazz, 
			ArrayType typeInfo,
			int cascade) {
		_localTransaction = localTransaction;
		_id = id;
		_clazz = clazz;
		_typeInfo = typeInfo;
		_cascade = cascade;
	}

	public void run(){
    	_localTransaction.checkSynchronization();
    	
        if(DTrace.enabled){
            DTrace.WRITE_UPDATE_ADJUST_INDEXES.log(_id);
        }
        
        if(alreadyHandled()){
        	return;
        }
        
        Slot slot = _localTransaction.getCurrentSlotOfID(_id);
        if(handledAsReAdd(slot)){
        	return;
        }
        
        if(handledWithNoChildIndexModification(slot)){
        	return;
        }
        
        StatefulBuffer objectBytes = (StatefulBuffer)container().readReaderOrWriterBySlot(localTransaction(), _id, false, slot);
        
        updateChildIndexes(objectBytes);
        
        freeSlotOnCommit(objectBytes);
	}

	private LocalObjectContainer container() {
		return _localTransaction.file();
	}

	private void freeSlotOnCommit(StatefulBuffer objectBytes) {
		_localTransaction.slotFreeOnCommit(_id, new Slot(objectBytes.getAddress(), objectBytes.length()));
	}

	private void updateChildIndexes(StatefulBuffer objectBytes) {
		ObjectHeader oh = new ObjectHeader(_clazz, objectBytes);
        
        DeleteInfo info = (DeleteInfo)TreeInt.find(_localTransaction._delete, _id);
        if(info != null){
            if(info._cascade > _cascade){
                _cascade = info._cascade;
            }
        }
        
        objectBytes.setCascadeDeletes(_cascade);
        
        DeleteContextImpl context = new DeleteContextImpl(objectBytes, oh, _clazz.classReflector(), null);
        _clazz.deleteMembers(context, _typeInfo, true);
	}

	private boolean handledAsReAdd(Slot slot) {
		if(slot != null  && !slot.isNull()){
			return false;
		}
        _clazz.addToIndex(localTransaction(), _id);
        return true;
	}
	
	private boolean alreadyHandled() {
		TreeInt newNode = new TreeInt(_id);
        _localTransaction._writtenUpdateAdjustedIndexes = Tree.add(_localTransaction._writtenUpdateAdjustedIndexes, newNode);
        return ! newNode.wasAddedToTree();
	}
	
	private boolean handledWithNoChildIndexModification(Slot slot) {
		if(! _clazz.canUpdateFast()){
			return false;
		}
    	_localTransaction.slotFreeOnCommit(_id, slot);
    	return true;
	}

	private LocalTransaction localTransaction() {
		return _localTransaction;
	}

}