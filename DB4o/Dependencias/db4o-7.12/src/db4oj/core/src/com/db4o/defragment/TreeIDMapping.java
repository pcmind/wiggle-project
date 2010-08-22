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
package com.db4o.defragment;

import com.db4o.foundation.*;
import com.db4o.internal.*;


/**
 * In-memory mapping for IDs during a defragmentation run.
 * 
 * @see Defragment
 */
public class TreeIDMapping extends AbstractContextIDMapping {
	
	private Tree _tree;
	
	public int mappedID(int oldID, boolean lenient) {
		int classID = mappedClassID(oldID);
		if(classID != 0) {
			return classID;
		}
		TreeIntObject res = (TreeIntObject) TreeInt.find(_tree, oldID);
		if(res != null){
			return ((Integer)res._object).intValue();
		}
		if(lenient){
			TreeIntObject nextSmaller = (TreeIntObject) Tree.findSmaller(_tree, new TreeInt(oldID));
			if(nextSmaller != null){
				int baseOldID = nextSmaller._key;
				int baseNewID = ((Integer)nextSmaller._object).intValue();
				return baseNewID + oldID - baseOldID; 
			}
		}
		return 0;
	}

	public void open() {
	}
	
	public void close() {
	}

	protected void mapNonClassIDs(int origID, int mappedID) {
		_tree = Tree.add(_tree, new TreeIntObject(origID, new Integer(mappedID)));
	}
}
