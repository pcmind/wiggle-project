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
package com.db4o.foundation;

import java.util.*;

/**
 * @sharpen.ignore
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class Iterator4JdkIterator implements Iterator{
    
    private static final Object BEFORE_START = new Object();
    
    private static final Object BEYOND_END = new Object();
    
    private final Iterator4 _delegate;
    
    private Object _current;
    
    public Iterator4JdkIterator(Iterator4 i){
        _delegate = i;
        _current = BEFORE_START; 
    }

    public boolean hasNext() {
        checkBeforeStart();
        return _current != BEYOND_END;
    }

    public Object next() {
        checkBeforeStart();
        if (_current == BEYOND_END){
            throw new NoSuchElementException();
        }
        Object result = _current;
        if(_delegate.moveNext()){
            _current = _delegate.current();
        }else{
            _current = BEYOND_END;
        }
        return result;
    }
    
    private void checkBeforeStart(){
        if(_current != BEFORE_START){
            return;
        }
        if(_delegate.moveNext()){
            _current = _delegate.current();
        }else{
            _current = BEYOND_END;
        }
    }

    public void remove() {
        throw new UnsupportedOperationException(); 
    }
    
}
