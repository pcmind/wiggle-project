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
 * @exclude
 */
public class TimeStampIdGenerator {
    
	private long _last;

	public static long idToMilliseconds(long id) {
		return id >> 15;
	}

	public TimeStampIdGenerator() {
		this(0);
	}

	public TimeStampIdGenerator(long minimumNext) {
		_last = minimumNext;
	}

	public long generate() {
		long t = System.currentTimeMillis();

		t = t << 15;

		if (t <= _last) {
			_last ++;
		} else {
			_last = t;
		}
		return _last;
	}

	public long last() {
		return _last;
	}

	public boolean setMinimumNext(long newMinimum) {
        if(newMinimum <= _last){
            return false;
        }
		_last = newMinimum;
        return true;
	}
}
