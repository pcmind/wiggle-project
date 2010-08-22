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
package com.db4o.db4ounit.common.cs;

import com.db4o.config.*;
import com.db4o.cs.internal.*;
import com.db4o.internal.*;

import db4ounit.extensions.*;

public class SwitchingToFileWithDifferentClassesTestCase extends StandaloneCSTestCaseBase {

	public static class Data1 {
		public int _id;

		public Data1(int id) {
			this._id = id;
		}

		@Override
        public boolean equals(Object obj) {
	        if (this == obj)
		        return true;
	        if (obj == null)
		        return false;
	        if (getClass() != obj.getClass())
		        return false;
	        Data1 other = (Data1) obj;
	        if (_id != other._id)
		        return false;
	        return true;
        }
	}
	
	public static class Data2 extends Data1 {
		public Data2(int id) {
	        super(id);
        }
	}
	
	@Override
	protected void configure(Configuration config) {
		config.reflectWith(Platform4.reflectorForType(Data1.class));
	}

	@Override
	protected void runTest() throws Throwable {
		
		ClientObjectContainer clientA = openClient();
		clientA.store(new Data1(1));
		
		ClientObjectContainer clientB = openClient();
		clientB.store(new Data1(2));
		clientB.commit();
		
		clientB.switchToFile(SwitchingFilesFromClientUtil.FILENAME_B);
		
		final Data2 data2 = new Data2(3);
		clientA.store(data2);
		clientA.commit();
		
		clientB.switchToMainFile();
		
		ObjectSetAssert.sameContent(clientB.query(Data2.class), data2);
		
	}

}
