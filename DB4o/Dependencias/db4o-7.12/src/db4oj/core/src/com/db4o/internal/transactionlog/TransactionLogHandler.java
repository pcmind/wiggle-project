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

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.slots.*;


/**
 * @exclude
 */
public abstract class TransactionLogHandler {
	
	
	protected LocalObjectContainer file(LocalTransaction trans) {
		return trans.file();
	}
	
    protected void flushDatabaseFile(LocalTransaction trans) {
		trans.flushFile();
	}
    
	protected void appendSlotChanges(LocalTransaction trans, final ByteArrayBuffer writer){
		trans.traverseSlotChanges(new Visitor4() {
			public void visit(Object obj) {
				((SlotChange)obj).write(writer);
			}
		});
    }
    
    protected int transactionLogSlotLength(LocalTransaction trans){
    	// slotchanges * 3 for ID, address, length
    	// 2 ints for slotlength and count
    	return ((countSlotChanges(trans) * 3) + 2) * Const4.INT_LENGTH;
    }

	protected int countSlotChanges(LocalTransaction trans){
        final IntByRef count = new IntByRef();
        trans.traverseSlotChanges(new Visitor4() {
			public void visit(Object obj) {
                SlotChange slot = (SlotChange)obj;
                if(slot.isSetPointer()){
                    count.value++;
                }
			}
		});
        return count.value;
	}

	public abstract Slot allocateSlot(LocalTransaction trans, boolean append);

	public abstract void applySlotChanges(LocalTransaction trans, Slot reservedSlot);

	public abstract boolean checkForInterruptedTransaction(LocalTransaction trans, ByteArrayBuffer reader);

	public abstract void completeInterruptedTransaction(LocalTransaction trans);

	public abstract void close();
	

}
