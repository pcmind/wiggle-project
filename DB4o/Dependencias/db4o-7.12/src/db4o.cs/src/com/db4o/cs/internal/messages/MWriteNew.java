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
package com.db4o.cs.internal.messages;

import com.db4o.cs.internal.*;
import com.db4o.internal.*;
import com.db4o.internal.slots.*;

public final class MWriteNew extends MsgObject implements ServerSideMessage {
	
	public final void processAtServer() {
        int classMetadataId = _payLoad.readInt();
        LocalObjectContainer container = (LocalObjectContainer)stream();
        unmarshall(_payLoad._offset);
        synchronized (streamLock()) {
            ClassMetadata classMetadata = classMetadataId == 0
            					? null
            					: container.classMetadataForID(classMetadataId);
            
            int id = _payLoad.getID();
            prefetchedIDConsumed(id);            
            
            transaction().slotFreePointerOnRollback(id);
            
            Slot slot = container.getSlot(_payLoad.length());
            _payLoad.address(slot.address());
            
            transaction().slotFreeOnRollback(id, slot);
            
            if(classMetadata != null){
                classMetadata.addFieldIndices(_payLoad,null);
            }
            container.writeNew(transaction(), _payLoad.pointer(), classMetadata, _payLoad);
            transaction().setPointer(id, slot);
        }
    }

	private void prefetchedIDConsumed(int id) {
		ServerMessageDispatcherImpl serverMessageDispatcher = (ServerMessageDispatcherImpl) serverMessageDispatcher();
		serverMessageDispatcher.prefetchedIDConsumed(id);
	}
}