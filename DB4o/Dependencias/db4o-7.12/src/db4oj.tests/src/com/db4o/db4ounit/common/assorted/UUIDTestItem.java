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

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.query.*;

import db4ounit.*;

/**
 * @exclude
 */
public class UUIDTestItem {
	public String name;

	public UUIDTestItem() {
	}

	public UUIDTestItem(String name) {
		this.name = name;
	}

	public static void assertItemsCanBeRetrievedByUUID(final ExtObjectContainer container, Hashtable4 uuidCache) {
		Query q = container.query();
		q.constrain(UUIDTestItem.class);
		ObjectSet objectSet = q.execute();
		while (objectSet.hasNext()) {
			UUIDTestItem item = (UUIDTestItem) objectSet.next();
			Db4oUUID uuid = container.getObjectInfo(item).getUUID();			
			Assert.isNotNull(uuid);
			Assert.areSame(item, container.getByUUID(uuid));
			final Db4oUUID cached = (Db4oUUID) uuidCache.get(item.name);
			if (cached != null) {
				Assert.areEqual(cached, uuid);
			} else {
				uuidCache.put(item.name, uuid);
			}
		}
	}
}