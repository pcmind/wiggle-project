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
package com.db4o.test.performance;

import java.io.*;

import com.db4o.*;

public class TransactionsPerSecond {
    
    public static void main(String[] args) {
        new TransactionsPerSecond().run();
    }
    
    public static class Item{
        public int _int;
        public Item(){
        }
        public Item(int int_){
            _int = int_;
        }
    }
    
    private static final String FILE = "tps.db4o";
    
    private static final long TOTAL_COUNT = 5000;
    
    public void run(){
        
        // This switch will make a big difference:
        Db4o.configure().flushFileBuffers(false);
        
        new File(FILE).delete();
        
        ObjectContainer objectContainer = Db4o.openFile(FILE).ext();
        
        long start = System.currentTimeMillis();
        
        for (int i = 0; i < TOTAL_COUNT; i++) {
            objectContainer.store(new Item(i));
            objectContainer.commit();
        }
        
        long stop = System.currentTimeMillis();
        long duration = stop - start;
        objectContainer.close();
        
        System.out.println("Time to store " + TOTAL_COUNT + " objects: " + duration + "ms");
        
        double seconds = ((double)duration) / ((double)1000); 
        double tps = TOTAL_COUNT / seconds;
        
        System.out.println("Transactions per second: " + tps);
    }

}
