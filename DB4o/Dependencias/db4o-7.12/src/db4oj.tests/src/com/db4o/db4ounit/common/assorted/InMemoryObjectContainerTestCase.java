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
package com.db4o.db4ounit.common.assorted;

import com.db4o.*;
import com.db4o.ext.*;

import db4ounit.*;


/**
 * @exclude
 */
public class InMemoryObjectContainerTestCase implements TestLifeCycle{
    
    private MemoryFile memoryFile;
    private ObjectContainer objectContainer;
    
    private static int STORED_ITEMS = 1000;

    /**
     * @deprecated using deprecated api
     */
    public void setUp() throws Exception {
        memoryFile = new MemoryFile();
        memoryFile.setIncrementSizeBy(100);
        memoryFile.setInitialSize(100);
        objectContainer = ExtDb4o.openMemoryFile(memoryFile);
    }
    
    public static class Item {
        
    }
    
    public void testSizeIncrement(){
        int lastSize = fileSize();
        for (int i = 0; i < STORED_ITEMS; i++) {
            objectContainer.store(new Item());
            Assert.isSmaller(lastSize + 1000, fileSize());
            lastSize = fileSize();
        }
    }

    private int fileSize() {
        return memoryFile.getBytes().length;
    }

    public void tearDown() throws Exception {
        
    }
    
}
