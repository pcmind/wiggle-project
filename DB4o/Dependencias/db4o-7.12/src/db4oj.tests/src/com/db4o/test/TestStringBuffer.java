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
package com.db4o.test;

import com.db4o.*;
import com.db4o.query.*;

/**
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class TestStringBuffer {
    
    public StringBuffer sb;
    
    public TestStringBuffer(){
        
    }
    
    public TestStringBuffer(String str){
        sb = new StringBuffer(str);
    }
    
    public void store(){
        Test.deleteAllInstances(this);
//        Test.store(new TestStringBuffer("Aloha"));
//        Test.store(new TestStringBuffer("Yohaa"));
        Test.store(new TestStringBuffer("Aloe Vera"));
//        Test.store(new TestStringBuffer("Store Aloha"));
    }
    
    public void test(){
        Query q = Test.query();
        q.constrain(new TestStringBuffer("Vera"));
        q.descend("sb").constraints().contains();
        ObjectSet os = q.execute();
        Test.ensure(os.size() == 1);
        TestStringBuffer tbs = (TestStringBuffer)os.next();
        Test.ensure(tbs.sb.toString().equals("Aloe Vera"));
        
//        q = Test.query();
//        q.constrain(new TestStringBuffer("Yohaa"));
//        os = q.execute();
//        Test.ensure(os.size() == 1);
//        tbs = (TestStringBuffer)os.next();
//        Test.ensure(tbs.sb.toString().equals("Yohaa"));
    }
}
