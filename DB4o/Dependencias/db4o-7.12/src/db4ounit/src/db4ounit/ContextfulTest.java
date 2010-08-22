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
package db4ounit;

import com.db4o.foundation.*;

import db4ounit.fixtures.*;

public class ContextfulTest extends Contextful implements Test {
	
	private final TestFactory _factory;

	public ContextfulTest(TestFactory factory) {
		_factory = factory;
	}

	public String label() {
		return (String)run(new Closure4() {
			public Object run() {
				return testInstance().label();
			}
		});
	}

	public void run() {
		run(new Runnable() {
			public void run() {
				testInstance().run();
			}
		});
	}

	private Test testInstance() {
		return _factory.newInstance();
	}
}
