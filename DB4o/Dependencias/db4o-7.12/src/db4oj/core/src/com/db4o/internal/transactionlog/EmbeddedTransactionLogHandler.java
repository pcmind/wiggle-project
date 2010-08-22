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
package com.db4o.internal.transactionlog;

import com.db4o.internal.*;
import com.db4o.internal.freespace.*;
import com.db4o.internal.slots.*;

/**
 * @exclude
 */
public class EmbeddedTransactionLogHandler extends TransactionLogHandler{
	
	private int _addressOfIncompleteCommit; 
	
	public boolean checkForInterruptedTransaction(LocalTransaction trans, ByteArrayBuffer reader) {
	    int transactionID1 = reader.readInt();
	    int transactionID2 = reader.readInt();
	    if( (transactionID1 > 0)  &&  (transactionID1 == transactionID2)){
	        _addressOfIncompleteCommit = transactionID1; 
	        return true;
	    }
		return false;
	}
	
	public void completeInterruptedTransaction(LocalTransaction trans) {
		StatefulBuffer bytes = new StatefulBuffer(trans, _addressOfIncompleteCommit, Const4.INT_LENGTH);
		bytes.read();
        int length = bytes.readInt();
        if (length > 0) {
            bytes = new StatefulBuffer(trans, _addressOfIncompleteCommit, length);
            bytes.read();
            bytes.incrementOffset(Const4.INT_LENGTH);
            trans.readSlotChanges(bytes);
            if(trans.writeSlots()){
                flushDatabaseFile(trans);
            }
            file(trans).writeTransactionPointer(0);
            flushDatabaseFile(trans);
            trans.freeSlotChanges(false);
        } else {
            file(trans).writeTransactionPointer(0);
            flushDatabaseFile(trans);
        }
	}

	public Slot allocateSlot(LocalTransaction trans, boolean appendToFile) {
		int transactionLogByteCount = transactionLogSlotLength(trans);
    	FreespaceManager freespaceManager = trans.freespaceManager();
		if(! appendToFile && freespaceManager != null){
    		int blockedLength = file(trans).bytesToBlocks(transactionLogByteCount);
    		Slot slot = freespaceManager.allocateTransactionLogSlot(blockedLength);
    		if(slot != null){
    			return file(trans).toNonBlockedLength(slot);
    		}
    	}
    	return file(trans).appendBytes(transactionLogByteCount);
	}

	private void freeSlot(LocalTransaction trans, Slot slot){
    	if(slot == null){
    		return;
    	}
    	if(trans.freespaceManager() == null){
    	    return;
    	}
    	trans.freespaceManager().freeTransactionLogSlot(file(trans).toBlockedLength(slot));
	}

	public void applySlotChanges(LocalTransaction trans, Slot reservedSlot) {
		int slotChangeCount = countSlotChanges(trans);
		if(slotChangeCount > 0){
				
		    Slot transactionLogSlot = slotLongEnoughForLog(trans, reservedSlot) ? reservedSlot
			    	: allocateSlot(trans, true);
	
			    final StatefulBuffer buffer = new StatefulBuffer(trans, transactionLogSlot);
			    buffer.writeInt(transactionLogSlot.length());
			    buffer.writeInt(slotChangeCount);
	
			    appendSlotChanges(trans, buffer);
	
			    buffer.write();
			    flushDatabaseFile(trans);
	
			    file(trans).writeTransactionPointer(transactionLogSlot.address());
			    flushDatabaseFile(trans);
	
			    if (trans.writeSlots()) {
			    	flushDatabaseFile(trans);
			    }
	
			    file(trans).writeTransactionPointer(0);
			    flushDatabaseFile(trans);
			    
			    if (transactionLogSlot != reservedSlot) {
			    	freeSlot(trans, transactionLogSlot);
			    }
		}
		freeSlot(trans, reservedSlot);
	}
	
	private boolean slotLongEnoughForLog(LocalTransaction trans, Slot slot){
    	return slot != null  &&  slot.length() >= transactionLogSlotLength(trans);
    }
    

	public void close() {
		// do nothing
	}

}
