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

import com.db4o.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class DeactivateTestCase extends AbstractDb4oTestCase  {
	protected void store() throws Exception {
		db().set(new Item("foo", new Item("bar", null)));
	}
	
	public void test() {		
		Query query = newQuery();
		query.descend("_name").constrain("foo");
		
		ObjectSet results = query.execute();
		Assert.areEqual(1, results.size());
		
		Item item1 = (Item) results.next();	
		Item item2 = item1._child;
		
		Assert.isTrue(db().isActive(item1));
		Assert.isTrue(db().isActive(item2));
		
		db().deactivate(item1);
		
		Assert.isFalse(db().isActive(item1));
		Assert.isTrue(db().isActive(item2));
	}
	
	public static void main(String []args) {
		new DeactivateTestCase().runAll();
	}
	
	public static class Item {
		public Item _child;
		public String _name;
		
		public Item(String name, Item child) {
			_name = name;
			_child = child;
		}
	}
}
