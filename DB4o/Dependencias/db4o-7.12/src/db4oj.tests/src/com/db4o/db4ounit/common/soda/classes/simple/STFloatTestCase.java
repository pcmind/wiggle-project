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
package com.db4o.db4ounit.common.soda.classes.simple;
import com.db4o.query.*;


public class STFloatTestCase extends com.db4o.db4ounit.common.soda.util.SodaBaseTestCase{
	
	public float i_float;
	
	public STFloatTestCase(){
	}
	
	private STFloatTestCase(float a_float){
		i_float = a_float;
	}
	
	public Object[] createData() {
		return new Object[]{
			new STFloatTestCase(Float.MIN_VALUE),
			new STFloatTestCase((float) 0.0000123),
			new STFloatTestCase((float) 1.345),
			new STFloatTestCase(Float.MAX_VALUE),
		};
	}
	
	public void testEquals(){
		Query q = newQuery();
		q.constrain(_array[0]); 
		com.db4o.db4ounit.common.soda.util.SodaTestUtil.expectOne(q, _array[0]);
	}
	
	public void testGreater(){
		Query q = newQuery();
		q.constrain(new STFloatTestCase((float)0.1));
		q.descend("i_float").constraints().greater();
		
		expect(q, new int[] { 2, 3});
	}
	
	public void testSmaller(){
		Query q = newQuery();
		q.constrain(new STFloatTestCase((float)1.5));
		q.descend("i_float").constraints().smaller();
		
		expect(q, new int[] {0, 1, 2});
	}
}

