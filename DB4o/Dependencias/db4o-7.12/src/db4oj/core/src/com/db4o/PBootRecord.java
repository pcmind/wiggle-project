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
package com.db4o;

import com.db4o.ext.*;
import com.db4o.internal.*;

/**
 * Old database boot record class. 
 * 
 * This class was responsible for storing the last timestamp id,
 * for holding a reference to the Db4oDatabase object of the 
 * ObjectContainer and for holding on to the UUID index.
 * 
 * This class is no longer needed with the change to the new
 * fileheader. It still has to stay here to be able to read
 * old databases.
 *
 * @exclude
 * @persistent
 */
public class PBootRecord extends P1Object implements Internal4{

    public Db4oDatabase       i_db;

    public long               i_versionGenerator;

    public MetaIndex          i_uuidMetaIndex;

    public MetaIndex getUUIDMetaIndex(){
        return i_uuidMetaIndex;
    }

    public void write(LocalObjectContainer file) {
    	// write is still called when storing objects to old
    	// database files (CLI1.ObjectInfoMigration52TestCase
    	// and CLI1.ObjectInfoMigration57)
    
    }

}