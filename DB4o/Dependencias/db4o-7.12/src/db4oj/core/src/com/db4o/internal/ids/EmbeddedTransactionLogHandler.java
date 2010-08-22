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
package com.db4o.internal.ids;

import com.db4o.internal.*;
import com.db4o.internal.freespace.*;
import com.db4o.internal.slots.*;

/**
 * @exclude
 */
public class EmbeddedTransactionLogHandler extends TransactionLogHandler{
	
	private int _addressOfIncompleteCommit; 
	
	public boolean checkForInterruptedTransaction(IdSystem idSystem, ByteArrayBuffer reader) {
	    int transactionID1 = reader.readInt();
	    int transactionID2 = reader.readInt();
	    if( (transactionID1 > 0)  &&  (transactionID1 == transactionID2)){
	        _addressOfIncompleteCommit = transactionID1; 
	        return true;
	    }
		return false;
	}
	
	public void completeInterruptedTransaction(IdSystem idSystem) {
		StatefulBuffer bytes = new StatefulBuffer(idSystem.systemTransaction(), _addressOfIncompleteCommit, Const4.INT_LENGTH);
		bytes.read();
        int length = bytes.readInt();
        if (length > 0) {
            bytes = new StatefulBuffer(idSystem.systemTransaction(), _addressOfIncompleteCommit, length);
            bytes.read();
            bytes.incrementOffset(Const4.INT_LENGTH);
            idSystem.readSlotChanges(bytes);
            if(idSystem.writeSlots()){
                flushDatabaseFile(idSystem);
            }
            file(idSystem).writeTransactionPointer(0);
            flushDatabaseFile(idSystem);
            idSystem.freeSlotChanges(false);
        } else {
            file(idSystem).writeTransactionPointer(0);
            flushDatabaseFile(idSystem);
        }
	}

	public Slot allocateSlot(IdSystem idSystem, boolean appendToFile) {
		int transactionLogByteCount = transactionLogSlotLength(idSystem);
    	FreespaceManager freespaceManager = idSystem.freespaceManager();
		if(! appendToFile && freespaceManager != null){
    		int blockedLength = file(idSystem).bytesToBlocks(transactionLogByteCount);
    		Slot slot = freespaceManager.allocateTransactionLogSlot(blockedLength);
    		if(slot != null){
    			return file(idSystem).toNonBlockedLength(slot);
    		}
    	}
    	return file(idSystem).appendBytes(transactionLogByteCount);
	}

	private void freeSlot(IdSystem idSystem, Slot slot){
    	if(slot == null){
    		return;
    	}
    	if(idSystem.freespaceManager() == null){
    	    return;
    	}
    	idSystem.freespaceManager().freeTransactionLogSlot(file(idSystem).toBlockedLength(slot));
	}

	public void applySlotChanges(IdSystem idSystem, Slot reservedSlot) {
		int slotChangeCount = countSlotChanges(idSystem);
		if(slotChangeCount > 0){
				
		    Slot transactionLogSlot = slotLongEnoughForLog(idSystem, reservedSlot) ? reservedSlot
			    	: allocateSlot(idSystem, true);
	
			    final StatefulBuffer buffer = new StatefulBuffer(idSystem.systemTransaction(), transactionLogSlot);
			    buffer.writeInt(transactionLogSlot.length());
			    buffer.writeInt(slotChangeCount);
	
			    appendSlotChanges(idSystem, buffer);
	
			    buffer.write();
			    flushDatabaseFile(idSystem);
	
			    file(idSystem).writeTransactionPointer(transactionLogSlot.address());
			    flushDatabaseFile(idSystem);
	
			    if (idSystem.writeSlots()) {
			    	flushDatabaseFile(idSystem);
			    }
	
			    file(idSystem).writeTransactionPointer(0);
			    flushDatabaseFile(idSystem);
			    
			    if (transactionLogSlot != reservedSlot) {
			    	freeSlot(idSystem, transactionLogSlot);
			    }
		}
		freeSlot(idSystem, reservedSlot);
	}
	
	private boolean slotLongEnoughForLog(IdSystem idSystem, Slot slot){
    	return slot != null  &&  slot.length() >= transactionLogSlotLength(idSystem);
    }
    

	public void close() {
		// do nothing
	}

}
