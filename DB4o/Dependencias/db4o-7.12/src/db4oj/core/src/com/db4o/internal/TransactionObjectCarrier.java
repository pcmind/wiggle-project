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

import com.db4o.internal.references.*;
import com.db4o.internal.slots.*;


/**
 * TODO: Check if all time-consuming stuff is overridden! 
 */
class TransactionObjectCarrier extends LocalTransaction{
	
	TransactionObjectCarrier(ObjectContainerBase container, Transaction parentTransaction, ReferenceSystem referenceSystem) {
		super(container, parentTransaction, referenceSystem);
	}
	
	public void commit() {
		// do nothing
	}
	
    public void slotFreeOnCommit(int id, Slot slot) {
//      do nothing
    }
    
    public void slotFreeOnRollback(int id, Slot slot) {
//      do nothing
    }
    
    void produceUpdateSlotChange(int id, Slot slot) {
        setPointer(id, slot);
    }
    
    void slotFreeOnRollbackCommitSetPointer(int id, Slot slot, boolean forFreespace) {
        setPointer(id, slot);
    }
    
    void slotFreePointerOnCommit(int a_id, Slot slot) {
//      do nothing
    }
    
    public void slotFreePointerOnCommit(int a_id) {
    	// do nothing
    }
	
	public void setPointer(int a_id, Slot slot) {
		writePointer(a_id, slot);
	}
    
    boolean supportsVirtualFields(){
        return false;
    }
    
    
    

}
