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
package com.db4o.db4ounit.common.assorted;

import com.db4o.config.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public class DatabaseGrowthSizeTestCase extends AbstractDb4oTestCase implements OptOutMultiSession {
	
	private static final int SIZE = 10000;
	
	private static final int APPROXIMATE_HEADER_SIZE = 100;

	public static void main(String[] args) {
		new DatabaseGrowthSizeTestCase().runSolo();
	}
	
	protected void configure(Configuration config) throws Exception {
		config.databaseGrowthSize(SIZE);
		config.blockSize(3);
	}
	
	public void test(){
		Assert.isGreater(SIZE, fileSession().fileLength());
		Assert.isSmaller(SIZE + APPROXIMATE_HEADER_SIZE, fileSession().fileLength());
		
		Item item = Item.newItem(SIZE);
		store(item);
		
		Assert.isGreater(SIZE * 2, fileSession().fileLength());
		Assert.isSmaller(SIZE * 2 + APPROXIMATE_HEADER_SIZE, fileSession().fileLength());
		
		Object retrievedItem = retrieveOnlyInstance(Item.class);
		Assert.areSame(item, retrievedItem);
	}
	
	public static class Item {
		
		public byte[] _payload;
		
		public Item(){
			
		}
		
		public static Item newItem(int payloadSize){
			Item item = new Item();
			item._payload = new byte[payloadSize];
			return item;
		}
		
	}

}
