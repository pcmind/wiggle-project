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
package com.db4o.internal.fileheader;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.internal.*;
import com.db4o.internal.activation.*;


/**
 * @exclude
 */
public class FileHeaderVariablePart1 extends PersistentBase{
    
    // The variable part format is:

    // (int) converter version
    // (byte) freespace system used
    // (int)  freespace address
    // (int) identity ID
    // (long) versionGenerator
	// (int) uuid index ID
    
    private static final int LENGTH = 1 + (Const4.INT_LENGTH * 4) + Const4.LONG_LENGTH + Const4.ADDED_LENGTH; 
    
    private final SystemData _systemData;
    
    public FileHeaderVariablePart1(int id, SystemData systemData) {
        setID(id);
        _systemData = systemData;
    }
    
    SystemData systemData() {
    	return _systemData;
    }

    public byte getIdentifier() {
        return Const4.HEADER;
    }

    public int ownLength() {
        return LENGTH;
    }

    public void readThis(Transaction trans, ByteArrayBuffer reader) {
        _systemData.converterVersion(reader.readInt());
        _systemData.freespaceSystem(reader.readByte());
        _systemData.freespaceAddress(reader.readInt());
        readIdentity((LocalTransaction) trans, reader.readInt());
        _systemData.lastTimeStampID(reader.readLong());
        _systemData.uuidIndexId(reader.readInt());
    }

    public void writeThis(Transaction trans, ByteArrayBuffer writer) {
        writer.writeInt(_systemData.converterVersion());
        writer.writeByte(_systemData.freespaceSystem());
        writer.writeInt(_systemData.freespaceAddress());
        writer.writeInt(_systemData.identity().getID(trans));
        writer.writeLong(_systemData.lastTimeStampID());
        writer.writeInt(_systemData.uuidIndexId());
    }
    
    private void readIdentity(LocalTransaction trans, int identityID) {
        LocalObjectContainer file = trans.file();
        Db4oDatabase identity = Debug4.staticIdentity ? Db4oDatabase.STATIC_IDENTITY : (Db4oDatabase) file.getByID(trans, identityID);
        if (null != identity) {
        	// TODO: what?
        	file.activate(trans, identity, new FixedActivationDepth(2));
        	_systemData.identity(identity);
        }
       
    }

}
