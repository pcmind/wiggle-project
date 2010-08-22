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

import com.db4o.internal.*;

/**
 * Kept for 5.7 migration support (PBootRecord depends on it).
 * 
 * @exclude
 * @persistent
 * @deprecated
 */
public class P1Object implements Db4oTypeImpl{
    
    public P1Object(){
    }
    
    public Object createDefault(Transaction a_trans) {
        throw Exceptions4.virtualException();
    }
    
    public boolean hasClassIndex() {
        return false;
    }	

    public void setTrans(Transaction a_trans){
    }

    public void setObjectReference(ObjectReference objectReference) {
    }
    
}
