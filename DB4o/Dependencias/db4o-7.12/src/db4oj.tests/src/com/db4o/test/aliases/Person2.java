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
package com.db4o.test.aliases;

/**
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class Person2 {

	private String _name;

	public Person2(String name) {
		_name = name;
	}
	
	public String name() {
		return _name;
	}
	
	public boolean equals(Object other) {
		if (other instanceof Person2) {
			return ((Person2)other)._name.equals(_name);
		}
		return false;
	}

}
