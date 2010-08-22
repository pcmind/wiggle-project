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
package com.db4o.db4ounit.jre11;

import db4ounit.extensions.*;

public class AllTests extends Db4oTestSuite {
	
	public static void main(String[] args) {
		System.exit(new AllTests().runAll());
	}

	protected Class[] testCases() {
		return new Class[] {
			com.db4o.db4ounit.common.AllTests.class,
			com.db4o.db4ounit.jre11.assorted.AllTests.class,
            com.db4o.db4ounit.jre11.btree.AllTests.class,
            com.db4o.db4ounit.jre11.defragment.AllTests.class,
            com.db4o.db4ounit.jre11.events.AllTests.class,
            com.db4o.db4ounit.jre11.handlers.AllTests.class,
            com.db4o.db4ounit.jre11.internal.AllTests.class,
            com.db4o.db4ounit.jre11.migration.AllTests.class,
            com.db4o.db4ounit.jre11.soda.AllTests.class,
            com.db4o.db4ounit.jre11.types.AllTests.class,
		};
	}
}
