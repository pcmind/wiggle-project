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

import com.db4o.config.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class PersistStaticFieldValuesTestCase extends AbstractDb4oTestCase {
	
    public static class Data {
        public static final PsfvHelper ONE = new PsfvHelper();
        public static final PsfvHelper TWO = new PsfvHelper();
        public static final transient PsfvHelper THREE = new PsfvHelper();
        
        /**
         * field put here to simulate a setup order failure during
         * ClassMetadata initialization.
         * @sharpen.ignore
         */
        public static final Class CLASS = Data.class; 

        public PsfvHelper one;
	    public PsfvHelper two;
	    public PsfvHelper three;
    }    

    protected void configure(Configuration config) {
        config.objectClass(Data.class).persistStaticFieldValues();
    }
    
    protected void store(){
        Data psfv = new Data();
        psfv.one = Data.ONE;
        psfv.two = Data.TWO;
        psfv.three = Data.THREE; 
        store(psfv);
    }
    
    public void test(){
        Data psfv = (Data)retrieveOnlyInstance(Data.class);
        Assert.areSame(Data.ONE,psfv.one);
        Assert.areSame(Data.TWO,psfv.two);
        Assert.areNotSame(Data.THREE,psfv.three);
    }
    
    public static class PsfvHelper{
    }
}
