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

import com.db4o.ext.*;

/**
 * @exclude
 */
public class SystemData {
    
    private int _classCollectionID;
    
    private int _converterVersion;
    
    private int _freespaceAddress;
    
    private int _freespaceID;
    
    private byte _freespaceSystem;
    
    private Db4oDatabase _identity;
    
    private long _lastTimeStampID;
    
    private byte _stringEncoding;

    private int _uuidIndexId;
    
    public int classCollectionID() {
        return _classCollectionID;
    }
    
    public void classCollectionID(int id) {
        _classCollectionID = id;
    }
    
    public int converterVersion(){
        return _converterVersion;
    }

    public void converterVersion(int version){
        _converterVersion = version;
    }
    
    public int freespaceAddress(){
        return _freespaceAddress;
    }
    
    public void freespaceAddress(int address){
        _freespaceAddress = address;
    }

    public int freespaceID() {
        return _freespaceID;
    }

    public void freespaceID(int id) {
        _freespaceID = id;
    }
    
    public byte freespaceSystem() {
        return _freespaceSystem;
    }
    
    public void freespaceSystem(byte freespaceSystemtype){
        _freespaceSystem = freespaceSystemtype;
    }
    
    public Db4oDatabase identity(){
        return _identity;
    }
    
    public void identity(Db4oDatabase identityObject) {
        _identity = identityObject;
    }

    public long lastTimeStampID(){
        return _lastTimeStampID;
    }
    
    public void lastTimeStampID(long id) {
        _lastTimeStampID = id;
    }
    
    public byte stringEncoding(){
        return _stringEncoding;
    }
    
    public void stringEncoding(byte encodingByte){
        _stringEncoding = encodingByte; 
    }
    
    public int uuidIndexId(){
        return _uuidIndexId;
    }
    
    public void uuidIndexId(int id){
        _uuidIndexId = id;
    }
    
}
