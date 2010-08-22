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
package com.db4o.db4ounit.jre11.migration;

import java.util.*;

import db4ounit.*;

public class DateMigrationTestCase extends MigrationTestCaseBase {
	
	public static class Item extends MigrationItem {
		public Date date;
		
		public Item() {
		}
		
		public Item(String name_, Date date_) {
			super(name_);
			date = date_;
		}
		
		public Object getValue() {
			return date;
		}
		
		public void setValue(Object value) {
			date = (Date)value;
		}
	}
	
	protected MigrationItem newItem(String name, Object value) {
		return new Item(name, (Date)value);
	}
	
	protected Object getMinValue() {
		return new Date(0);
	}

	protected Object getMaxValue() {
		return new Date(Long.MAX_VALUE - 1);
	}

	protected Object getOrdinaryValue() {
		return new Date(1166839200000L);
	}
	
	protected Object getUpdateValue() {
	    return new Date(1296839800000L);
	}

	protected String getDatabaseFileName() {
		return "dates.db4o";
	}
	
	public static void main(String[] args) {
		// reference db4o 5.2 and uncomment the line below
		// if you ever need to regenerate the file
		// new DateMigrationTestCase().generateFile();

		new ConsoleTestRunner(DateMigrationTestCase.class).run();
	}
}
