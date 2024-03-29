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
package com.db4o.db4ounit.jre12.collections;

import java.util.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class DeleteFromMapTestCase extends AbstractDb4oTestCase{
	
	public static class Holder {
		public Map<Item, Item> _map = new HashMap<Item, Item>();
	}
	
	public static class Item {
		
	}
	
	@Override
	protected void store() throws Exception {
		Holder holder = new Holder();
		Item item = new Item();
		holder._map.put(item, item);
		store(holder);
	}
	
	public void test(){
		Item item = retrieveOnlyInstance(Item.class);
		db().delete(item);
		db().commit();
		Holder holder = retrieveOnlyInstance(Holder.class);
		Assert.areEqual(0, holder._map.size());
	}

}
