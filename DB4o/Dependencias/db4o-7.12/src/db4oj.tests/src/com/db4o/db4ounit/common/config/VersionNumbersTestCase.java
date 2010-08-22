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
package com.db4o.db4ounit.common.config;

import com.db4o.config.*;
import com.db4o.ext.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class VersionNumbersTestCase extends AbstractDb4oTestCase{
	
	public static class Item {
		public String _name;
	}
	
	protected void configure(Configuration config) throws Exception {
		config.generateVersionNumbers(ConfigScope.GLOBALLY);
	}
	
	protected void store() throws Exception {
		Item item = new Item();
		item._name = "original";
		store(item);
	}
	
	public void test(){
		Item item = (Item) retrieveOnlyInstance(Item.class);
		ObjectInfo objectInfo = db().getObjectInfo(item);
		long version1 = objectInfo.getVersion();
		item._name = "modified";
		db().store(item);
		db().commit();
		long version2 = objectInfo.getVersion();
		Assert.isGreater(version1, version2);
		db().store(item);
		db().commit();
		objectInfo = db().getObjectInfo(item);
		long version3 = objectInfo.getVersion();
		Assert.isGreater(version2, version3);
	}

}
