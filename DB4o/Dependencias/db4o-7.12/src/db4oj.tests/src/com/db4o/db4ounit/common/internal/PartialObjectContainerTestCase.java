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
package com.db4o.db4ounit.common.internal;


import com.db4o.config.*;
import com.db4o.internal.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;


/**
 * @exclude
 */
public class PartialObjectContainerTestCase
	extends AbstractDb4oTestCase
	implements OptOutTA, OptOutInMemory {
    
    public static void main(String[] arguments) {
        new PartialObjectContainerTestCase().runSolo();
    }
    
    protected void configure(Configuration config) throws Exception {
        config.blockSize(8);
    }
    
    public void testBlocksToBytes(){
        int[] blocks = new int[]{0, 1, 8, 9};
        int[] bytes  = new int[]{0, 8, 64, 72};
        
        for (int i = 0; i < blocks.length; i++) {
            Assert.areEqual(bytes[i], localContainer().blocksToBytes(blocks[i]));
        }
    }
    
    public void testBytesToBlocks(){
        int[] bytes  = new int[]{0, 1, 2, 7, 8, 9, 16, 17, 799, 800, 801};
        int[] blocks = new int[]{0, 1, 1, 1, 1, 2,  2,  3, 100, 100, 101};
        
        for (int i = 0; i < blocks.length; i++) {
            Assert.areEqual(blocks[i], localContainer().bytesToBlocks(bytes[i]));
        }
    }

    private ObjectContainerBase localContainer() {
        return stream().container();
    }
}