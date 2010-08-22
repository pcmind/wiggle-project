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

import com.db4o.ext.*;


public class StorePrimitiveDirectly {
    
    public void test(){
        
        ExtObjectContainer oc = Test.objectContainer();
        
        boolean exceptionOccurred = false;
        
        oc.configure().exceptionsOnNotStorable(false);

        try{
            oc.store(new Integer(1));
        }catch(ObjectNotStorableException onse){
            
           // happens now in test compbination but shouldn't
            
            exceptionOccurred = true;
            
        }
        
        // FIXME: 
        
        // Test.ensure(!exceptionOccurred);

        
        oc.configure().exceptionsOnNotStorable(true);
        
        exceptionOccurred = false;
        
        try{
            oc.store(new Integer(1));
        }catch(ObjectNotStorableException onse){
            exceptionOccurred = true;
        }
        
        
        // FIXME: 
        
        // Test.ensure(exceptionOccurred);
        
        oc.configure().exceptionsOnNotStorable(false);
    }

}