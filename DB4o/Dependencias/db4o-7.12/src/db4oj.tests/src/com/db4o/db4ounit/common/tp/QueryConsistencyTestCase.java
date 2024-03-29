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
package com.db4o.db4ounit.common.tp;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.query.*;
import com.db4o.ta.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class QueryConsistencyTestCase extends AbstractDb4oTestCase {

	public static void main(String[] args) {
	    new QueryConsistencyTestCase().runNetworking();
    }
	
	@Override
	protected void store() throws Exception {
	    store(new Item("42"));
	}
	
	@Override
	protected void configure(Configuration config) throws Exception {
	    config.add(new TransparentPersistenceSupport());
	    config.optimizeNativeQueries(false);
	}
	
	public void testUpdate() {		
		final Item found = sodaQueryForItem("42");
		Assert.areEqual("42", found.getName());
		Assert.areSame(found, nativeQueryForItem("42"));
		
		found.setName("21");
		
		Assert.areSame(found, sodaQueryForItem("21"));
		Assert.areSame(found, nativeQueryForItem("21"));
		Assert.isNull(sodaQueryForItem("42"));
		
		db().commit();
		
		Assert.areSame(found, nativeQueryForItem("21"));
	}
	
	private Item nativeQueryForItem(final String name) {
		final ObjectSet<Item> result = db().query(new ItemByName(name));
		return firstOrNull(result);
	}
	
	public static final class ItemByName extends Predicate<Item> {
		public String name;
		
		public ItemByName(String name) {
			this.name = name;
		}
		
		@Override
		public boolean match(Item candidate) {
			return candidate.getName().equals(name);
		}
	}

	private Item sodaQueryForItem(final String id) {
	    final Query q = db().query();
	    q.constrain(Item.class);
	    q.descend("name").constrain(id);
	    return firstOrNull(q.<Item>execute());
    }

	private <T> T firstOrNull(final ObjectSet<T> result) {
		return result.hasNext() ? result.next() : null;
    }
}
