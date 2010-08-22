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

public class TestRunner {
	
	private final Iterable4 _tests;

	public TestRunner(Iterable4 tests) {
		_tests = tests;
	}

	public void run(TestListener listener) {
		
		listener.runStarted();
		
		final Iterator4 iterator = _tests.iterator();
		while (iterator.moveNext()) {
			final Test test = (Test)iterator.current();
			listener.testStarted(test);
			try {
				test.run();
			} catch (TestException x) {
			    Throwable reason = x.getReason();
				listener.testFailed(test, reason == null ? x : reason);
			} catch (Exception failure) {
				listener.testFailed(test, failure);
			}
		}
		
		listener.runFinished();
	}

}
