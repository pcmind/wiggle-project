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
package com.db4o.types;

import java.util.*;

/**
 *  db4o Map implementation for database-aware maps.
 * <br/><br/>
 * A <code>Db4oMap</code> supplies the methods specified in java.util.Map.<br/><br/>
 * All access to the map is controlled by the {@link com.db4o.ObjectContainer ObjectContainer} to help the
 * programmer produce expected results with as little work as possible:<br/>  
 * - newly added objects are automatically persisted.<br/>
 * - map elements are automatically activated when they are needed. The activation
 * depth is configurable with {@link Db4oCollection#activationDepth(int)}.<br/>
 * - removed objects can be deleted automatically, if the list is configured
 * with {@link Db4oCollection#deleteRemoved(boolean)}<br/><br/>
 * Usage:<br/>
 * - declare a <code>java.util.Map</code> variable on your persistent classes.<br/>
 * - fill this variable with a method in the ObjectContainer collection factory.<br/><br/>
 * <b>Example:</b><br/><br/>
 * <code>class MyClass{<br/>
 * &nbsp;&nbsp;Map myMap;<br/>
 * }<br/><br/>
 * MyClass myObject = new MyClass();<br/> 
 * myObject.myMap = objectContainer.ext().collections().newHashMap();
 * @deprecated since 7.0
 */
@decaf.IgnoreImplements(value=decaf.Platform.JDK11, interfaces=Map.class)
public interface Db4oMap extends Db4oCollection, Map {

}
