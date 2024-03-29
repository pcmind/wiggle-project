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
package com.db4o.internal;

/**
 * @exclude
 */
public class ArrayType {
	
	public static final ArrayType NONE = new ArrayType(0);
	
	public static final ArrayType PLAIN_ARRAY = new ArrayType(3);
	
	public static final ArrayType MULTIDIMENSIONAL_ARRAY = new ArrayType(4);
	
	private ArrayType(int value){
		_value = value;
	}
	
	private final int _value;
	
	public int value(){
		return _value;
	}
	
	public static ArrayType forValue(int value){
		switch(value){
			case 0:
				return NONE;
			case 3:
				return PLAIN_ARRAY;
			case 4:
				return MULTIDIMENSIONAL_ARRAY;
			default:
				throw new IllegalArgumentException();
		}
		
	}
	
}
