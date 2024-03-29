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
package com.db4o.internal.btree;

import com.db4o.foundation.*;


public interface BTreeRange {
	
	/**
	 * Iterates through all the valid pointers in 
	 * this range.
	 * @return an Iterator4 over BTreePointer value
	 */
	public Iterator4 pointers();
	
	public Iterator4 keys();

	public int size();

	public BTreeRange greater();

	public BTreeRange union(BTreeRange other);

	public BTreeRange extendToLast();

	public BTreeRange smaller();

	public BTreeRange extendToFirst();

	public BTreeRange intersect(BTreeRange range);

	public BTreeRange extendToLastOf(BTreeRange upperRange);

	public boolean isEmpty();
	
	public void accept(BTreeRangeVisitor visitor);

	public BTreePointer lastPointer();
}
