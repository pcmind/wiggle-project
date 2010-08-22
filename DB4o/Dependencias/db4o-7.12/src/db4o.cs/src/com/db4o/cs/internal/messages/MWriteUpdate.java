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

import com.db4o.internal.*;
import com.db4o.internal.activation.*;
import com.db4o.internal.slots.*;

public final class MWriteUpdate extends MsgObject implements ServerSideMessage {
	
	public final void processAtServer() {
	    int classMetadataID = _payLoad.readInt();
	    int arrayTypeValue = _payLoad.readInt();
	    ArrayType arrayType = ArrayType.forValue(arrayTypeValue);
	    LocalObjectContainer container = (LocalObjectContainer)stream();
	    unmarshall(_payLoad._offset);
	    synchronized(streamLock()){
	        ClassMetadata classMetadata = container.classMetadataForID(classMetadataID);
			int id = _payLoad.getID();
			transaction().writeUpdateAdjustIndexes(id, classMetadata, arrayType, 0);
			transaction().dontDelete(id);
            Slot oldSlot = ((LocalTransaction)transaction()).getCommittedSlotOfID(id);
            container.getSlotForUpdate(_payLoad);
			classMetadata.addFieldIndices(_payLoad, oldSlot);
            _payLoad.writeEncrypt();
            deactivateCacheFor(id);            
		}
	}

	private void deactivateCacheFor(int id) {
		transaction().deactivate(id, new FixedActivationDepth(1));
	}
}