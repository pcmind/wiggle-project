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
package com.db4o.db4ounit.common.acid;

import java.io.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.foundation.*;
import com.db4o.foundation.io.*;
import com.db4o.internal.*;
import com.db4o.io.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.fixtures.*;


// TODO: survives commenting out invocations of IoAdaptedObjectContainer#syncFiles() other
// than the one in LocalTransaction#flushFile()
public class CrashSimulatingTestCase implements TestCase, OptOutMultiSession {	
    
	public static class CrashData {
	    public String _name;	    
	    public CrashData _next;

	    public CrashData(CrashData next_, String name) {
	        _next = next_;
	        _name = name;
	    }
	    
		public String toString() {
			return _name+" -> "+_next;
		}
	}
	
    static final boolean VERBOSE = false;
    
    
    /**
     * @sharpen.remove
     */
    public void testWithCacheWithoutLogFile() throws IOException{
        doTest(true, false);
    }
    
    /**
     * @sharpen.remove
     */
    public void testWithCacheWithLogFile() throws IOException{
        doTest(true, true);
    }
    
    /**
     * @sharpen.remove
     */
    public void testWithoutCacheWithoutLogFile() throws IOException{
        doTest(false, false);
    }
    
    /**
     * @sharpen.remove
     */
    public void testWithoutCacheWithLogFile() throws IOException{
        doTest(false, true);
    }

    
    private void doTest(boolean cached, boolean useLogFile) throws IOException{
    	if(Platform4.needsLockFileThread()){
    		System.out.println("CrashSimulatingTestCase is ignored on platforms with lock file thread.");
    		return;
    	}
    	
        String path = Path4.combine(Path4.getTempPath(), "crashSimulate");
        String fileName = Path4.combine(path, "cs");
        
    	File4.delete(fileName);
    	File4.mkdirs(path);
        
    	createFile(baseConfig(useLogFile), fileName);
        
        CrashSimulatingStorage crashSimulatingStorage = new CrashSimulatingStorage(new FileStorage(), fileName);
        Storage storage = cached ? (Storage) new CachingStorage(crashSimulatingStorage) : crashSimulatingStorage;
        
        Configuration recordConfig = baseConfig(useLogFile);
        recordConfig.storage(storage);
        
        ObjectContainer oc = Db4o.openFile(recordConfig, fileName);
        
        ObjectSet objectSet = oc.queryByExample(new CrashData(null, "three"));
        oc.delete(objectSet.next());
        
        oc.store(new CrashData(null, "four"));
        oc.store(new CrashData(null, "five"));
        oc.store(new CrashData(null, "six"));
        oc.store(new CrashData(null, "seven"));
        oc.store(new CrashData(null, "eight"));
        oc.store(new CrashData(null, "nine"));
        oc.store(new CrashData(null, "10"));
        oc.store(new CrashData(null, "11"));
        oc.store(new CrashData(null, "12"));
        oc.store(new CrashData(null, "13"));
        oc.store(new CrashData(null, "14"));
        
        oc.commit();
        
        Query q = oc.query();
        q.constrain(CrashData.class);
        objectSet = q.execute();
        while(objectSet.hasNext()){
        	CrashData cData = (CrashData) objectSet.next();
            if( !  (cData._name.equals("10") || cData._name.equals("13")) ){
                oc.delete(cData);
            }
        }
        
        oc.commit();

        oc.close();

        int count = crashSimulatingStorage._batch.writeVersions(fileName);

        checkFiles(useLogFile, fileName, "R", crashSimulatingStorage._batch.numSyncs());
        checkFiles(useLogFile, fileName, "W", count);
		if (VERBOSE) {
			System.out.println("Total versions: " + count);
		}
    }

	private Configuration baseConfig(boolean useLogFile) {
		Config4Impl config = (Config4Impl) Db4o.newConfiguration();
		config.objectClass(CrashData.class).objectField("_name").indexed(true);
    	config.reflectWith(Platform4.reflectorForType(CrashSimulatingTestCase.class));
        config.bTreeNodeSize(4);
        config.lockDatabaseFile(false);
    	config.fileBasedTransactionLog(useLogFile);
		return config;
	}

	private void checkFiles(boolean useLogFile, String fileName, String infix,int count) {
        for (int i = 1; i <= count; i++) {
            if(VERBOSE){
                System.out.println("Checking " + infix + i);
            }
            String versionedFileName = fileName + infix + i;
            ObjectContainer oc = Db4o.openFile(baseConfig(useLogFile), versionedFileName);
	        try {
	            if(! stateBeforeCommit(oc)){
	                if(! stateAfterFirstCommit(oc)){
	                    Assert.isTrue(stateAfterSecondCommit(oc));
	                }
	            }
            } finally {
            	oc.close();
            }
        }
    }
    
    private boolean stateBeforeCommit(ObjectContainer oc){
        return expect(oc, new String[] {"one", "two", "three"});
    }
    
    private boolean stateAfterFirstCommit (ObjectContainer oc){
        return expect(oc, new String[] {"one", "two", "four", "five", "six", "seven", "eight", "nine", "10", "11", "12", "13", "14" });
    }
    
    private boolean stateAfterSecondCommit (ObjectContainer oc){
        return expect(oc, new String[] {"10", "13"});
    }
    
    private boolean expect(ObjectContainer container, String[] names){
    	Collection4 expected = new Collection4(names);
        ObjectSet actual = container.query(CrashData.class);
        while (actual.hasNext()){
            CrashData current = (CrashData)actual.next();
            if (! expected.remove(current._name)) {
            	return false;
            }
        }
        return expected.isEmpty();
    }
    
    private void createFile(Configuration config, String fileName) throws IOException{
        ObjectContainer oc = Db4o.openFile(config, fileName);
        try {
        	populate(oc);
        } finally {
        	oc.close();
        }
        File4.copy(fileName, fileName + "0");
    }

	private void populate(ObjectContainer container) {
		for (int i = 0; i < 10; i++) {
            container.store(new Item("delme"));
        }
        CrashData one = new CrashData(null, "one");
        CrashData two = new CrashData(one, "two");
        CrashData three = new CrashData(one, "three");
        container.store(one);
        container.store(two);
        container.store(three);
        container.commit();
        ObjectSet objectSet = container.query(Item.class);
        while(objectSet.hasNext()){
            container.delete(objectSet.next());
        }
	}
	
    public static class Item{
    	
        public String name;
        
        public Item() {
        }
        
        public Item(String name_) {
            this.name = name_;
        }

        public String getName() {
            return name;
        }
        
        public void setName(String name_){
        	name = name_;
        }

    }

	
}
