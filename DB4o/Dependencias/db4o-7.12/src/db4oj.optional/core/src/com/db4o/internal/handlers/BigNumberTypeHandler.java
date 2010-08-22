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
package com.db4o.internal.handlers;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.delete.*;
import com.db4o.internal.marshall.*;
import com.db4o.internal.slots.*;
import com.db4o.marshall.*;
import com.db4o.typehandlers.*;

/**
 * @sharpen.ignore
 * @exclude
 */
public abstract class BigNumberTypeHandler<TBigNumber> implements ValueTypeHandler, VariableLengthTypeHandler, QueryableTypeHandler, IndexableTypeHandler {

	public void defragment(DefragmentContext context) {
		skip(context);
	}

	public void delete(DeleteContext context) throws Db4oIOException {
		skip(context);
	}

	public Object read(ReadContext context) {
		return unmarshall(context);
	}
	
	public void write(WriteContext context, Object obj) {
		byte[] data = toByteArray((TBigNumber)obj);
		context.writeInt(data.length);
		context.writeBytes(data);
	}

	public PreparedComparison<Object> prepareComparison(final Context context, Object obj) {
		final TBigNumber value = obj instanceof TransactionContext
			? bigNumberFrom(((TransactionContext)obj)._object, context)
			: bigNumberFrom(obj, context);
			
		return new PreparedComparison<Object>() {
			public int compareTo(Object other) {
				if (other == null) {
					return (value == null ? 0 : 1);
				}
				if(value == null) {
					return -1;
				}
			    return compare(value, bigNumberFrom(other, context));
			}
		};
	}

	public boolean descendsIntoMembers() {
		return false;
	}
	
	public void defragIndexEntry(DefragmentContextImpl context) {
		context.copyID(false,true);
		context.incrementIntSize();
	}

    public int linkLength() {
        return Const4.INDIRECTION_LENGTH;
    }

	public Object readIndexEntry(Context context, ByteArrayBuffer reader) {
    	Slot s = new Slot(reader.readInt(), reader.readInt());
    	if (isInvalidSlot(s)){
    		return null;
    	}
    	return s; 
	}

	public void writeIndexEntry(Context context, ByteArrayBuffer writer, Object entry) {
        if(entry == null){
            writer.writeInt(0);
            writer.writeInt(0);
            return;
        }
         if(entry instanceof StatefulBuffer){
             StatefulBuffer entryAsWriter = (StatefulBuffer)entry;
             writer.writeInt(entryAsWriter.getAddress());
             writer.writeInt(entryAsWriter.length());
             return;
         }
         if(entry instanceof Slot){
             Slot s = (Slot) entry;
             writer.writeInt(s.address());
             writer.writeInt(s.length());
             return;
         }
         throw new IllegalArgumentException();
	}

	public Object indexEntryToObject(Context context, Object indexEntry) {
        if(indexEntry instanceof Slot){
            Slot slot = (Slot)indexEntry;
            indexEntry = bufferFromSlot(context, slot);
        }
        return unmarshall((ReadBuffer)indexEntry);
	}

	public Object readIndexEntry(ObjectIdContext context) throws CorruptionException, Db4oIOException {
        int payLoadOffSet = context.readInt();
        int length = context.readInt();
        if(payLoadOffSet == 0){
            return null;
        }
        return ((StatefulBuffer)context.buffer()).readPayloadWriter(payLoadOffSet, length);
	}

	public Object readIndexEntryFromObjectSlot(MarshallerFamily mf, StatefulBuffer buffer) throws CorruptionException, Db4oIOException {
        int payLoadOffSet = buffer.readInt();
        int length = buffer.readInt();
        if(payLoadOffSet == 0){
            return null;
        }
        return buffer.readPayloadWriter(payLoadOffSet, length);
	}

	protected abstract TBigNumber fromByteArray(byte[] data);
	
	protected abstract byte[] toByteArray(TBigNumber obj);
	
	protected abstract int compare(TBigNumber x, TBigNumber y);

	private ByteArrayBuffer bufferFromSlot(Context context, Slot slot) {
		return context.transaction().container().decryptedBufferByAddress(slot.address(), slot.length());
	}

	private TBigNumber bigNumberFrom(Object obj, Context context) {
		if(obj instanceof Slot) {
			obj = bufferFromSlot(context, (Slot)obj);
		}
		if(obj instanceof ReadBuffer) {
			ReadBuffer buffer = (ReadBuffer)obj;
			int offset = buffer.offset();
			buffer.seek(0);
			TBigNumber number = unmarshall(buffer);
			buffer.seek(offset);
			return number;
		}
		return (TBigNumber)obj;
    }
	
	private void skip(ReadBuffer context) {
		int numBytes = context.readInt();
		context.seek(context.offset() + numBytes);
	}

	private boolean isInvalidSlot(Slot slot) {
		return slot.isNull();
	}

	private TBigNumber unmarshall(final ReadBuffer buffer) {
	    byte[] data = new byte[buffer.readInt()];
		buffer.readBytes(data);
		return fromByteArray(data);
    }

}
