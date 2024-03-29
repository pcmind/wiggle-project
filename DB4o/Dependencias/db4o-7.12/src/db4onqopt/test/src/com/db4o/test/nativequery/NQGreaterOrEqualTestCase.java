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
package com.db4o.test.nativequery;

import com.db4o.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class NQGreaterOrEqualTestCase extends AbstractDb4oTestCase{
	
	public static class Item{
		
		public Item(long lowerLong, long upperLong) {
			_lowerLong = lowerLong;
			_upperLong = upperLong;
		}

		public long _lowerLong;
		
		public long _upperLong;
		
	}
	
	@Override
	protected void store() throws Exception {
		store(new Item(1, 100));
		store(new Item(50,300));
	}
	
	public void testNativeQueryForLong(){
		final long searchedLong = 20;
		ObjectSet<Item> objectSet = searchLong(searchedLong);
		Assert.areEqual(1, objectSet.size());
	}

	private ObjectSet<Item> searchLong(final long searchedLong) {
		ObjectSet<Item> objectSet = db().query(new Predicate<Item>() {
			@Override
			public boolean match(Item candidate) {
				return searchedLong>=candidate._lowerLong && searchedLong<=candidate._upperLong;
			}
		});
		return objectSet;
	}

}
