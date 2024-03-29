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

import com.db4o.internal.*;

/**
 * @exclude
 */
public class BTreeCancelledRemoval extends BTreeUpdate {
    
    private final Object _newKey;
    
	public BTreeCancelledRemoval(Transaction transaction, Object originalKey, Object newKey, BTreeUpdate existingPatches) {
		super(transaction, originalKey);
		_newKey = newKey;
		if (null != existingPatches) {
			append(existingPatches);
		}
	}
	
	protected void committed(BTree btree) {
	    // do nothing
	}
	
	public boolean isCancelledRemoval() {
		return true;
	}
    
    public String toString() {
        return "(u) " + super.toString();
    }

	protected Object getCommittedObject() {
		return _newKey;
	}

    protected void adjustSizeOnRemovalByOtherTransaction(BTree btree, BTreeNode node) {
        // The other transaction reduces the size, this entry ignores.
    }

	protected int sizeDiff() {
		return 1;
	}
	
}
