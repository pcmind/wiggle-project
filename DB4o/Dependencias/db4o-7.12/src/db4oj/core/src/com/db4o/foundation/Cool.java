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
package com.db4o.foundation;

/**
 * A collection of cool static methods that should be part of the runtime environment but are not.
 * 
 * @exclude
 */
public class Cool {

	public static void sleepIgnoringInterruption(long millis) {
		try {
			Thread.sleep(millis);
		} catch (Exception ignored) {
   		}
	}
	
	/**
	 * Keeps executing a block of code until it either returns false or millisecondsTimeout
	 * elapses.
	 * 
	 * @param millisecondsTimeout
	 * @param block
	 */
	public static void loopWithTimeout(long millisecondsTimeout, ConditionalBlock block) {
		final StopWatch watch = new AutoStopWatch();
		do {
			if (!block.run()) {
				break;
			}
		} while (watch.peek() < millisecondsTimeout);
	}

}
