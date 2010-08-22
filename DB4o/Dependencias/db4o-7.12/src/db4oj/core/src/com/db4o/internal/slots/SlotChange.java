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
package com.db4o.internal.slots;

import com.db4o.*;
import com.db4o.internal.*;

/**
 * @exclude
 */
public class SlotChange extends TreeInt {

	private int _action;

	private Slot _newSlot;

	private ReferencedSlot _shared;

	private static final int FREE_ON_COMMIT_BIT = 1;

	private static final int FREE_ON_ROLLBACK_BIT = 2;

	private static final int SET_POINTER_BIT = 3;
	
	private static final int FREE_POINTER_ON_COMMIT_BIT = 4;
	
    private static final int FREE_POINTER_ON_ROLLBACK_BIT = 5; 
    
    private static final int FREESPACE_BIT = 6; 
    
	public SlotChange(int id) {
		super(id);
	}

	public Object shallowClone() {
		SlotChange sc = new SlotChange(0);
		sc._action = _action;
		sc._newSlot = _newSlot;
		sc._shared = _shared;
		return super.shallowCloneInternal(sc);
	}

	private final void doFreeOnCommit() {
		setBit(FREE_ON_COMMIT_BIT);
	}

	private final void doFreeOnRollback() {
		setBit(FREE_ON_ROLLBACK_BIT);
	}
	
	private final void doFreePointerOnCommit(){
		setBit(FREE_POINTER_ON_COMMIT_BIT);
	}
	
	private final void doFreePointerOnRollback(){
		setBit(FREE_POINTER_ON_ROLLBACK_BIT);
	}

	private final void doSetPointer() {
		setBit(SET_POINTER_BIT);
	}

	public void freeDuringCommit(LocalObjectContainer file, boolean forFreespace) {
        if (isFreeOnCommit() && (isForFreeSpace() == forFreespace)) {
            file.freeDuringCommit(_shared, _newSlot);
        }
	}

	public final void freeOnCommit(LocalObjectContainer file, Slot slot) {

		if (_shared != null) {

			// second call or later.
			// The object has already been rewritten once, so we can free
			// directly

			file.free(slot);
			return;
		}

		doFreeOnCommit();

		ReferencedSlot refSlot = file.produceFreeOnCommitEntry(_key);

		if (refSlot.addReferenceIsFirst()) {
			refSlot.pointTo(slot);
		}

		_shared = refSlot;
	}
	
	public void freeOnRollback(Slot slot) {
		doFreeOnRollback();
		_newSlot = slot;
	}

	public void freeOnRollbackSetPointer(Slot slot) {
		doSetPointer();
		freeOnRollback(slot);
	}

	public void freePointerOnCommit() {
		doFreePointerOnCommit();
	}
	
	public void freePointerOnRollback() {
		doFreePointerOnRollback();
	}

	private final boolean isBitSet(int bitPos) {
		return (_action | (1 << bitPos)) == _action;
	}

	public boolean isDeleted() {
		return isSetPointer() && _newSlot.isNull();
	}
	
	public boolean isNew() {
		return isFreePointerOnRollback();
	}
    
    private final boolean isForFreeSpace() {
        return isBitSet(FREESPACE_BIT);
    }
    
	private final boolean isFreeOnCommit() {
		return isBitSet(FREE_ON_COMMIT_BIT);
	}

	private final boolean isFreeOnRollback() {
		return isBitSet(FREE_ON_ROLLBACK_BIT);
	}

	public final boolean isSetPointer() {
		return isBitSet(SET_POINTER_BIT);
	}
	
	/**
	 * FIXME:	Check where pointers should be freed on commit.
	 * 			This should be triggered in this class.
	 */
//	private final boolean isFreePointerOnCommit() {
//		return isBitSet(FREE_POINTER_ON_COMMIT_BIT);
//	}

	public final boolean isFreePointerOnRollback() {
		return isBitSet(FREE_POINTER_ON_ROLLBACK_BIT);
	}

	public Slot newSlot() {
		return _newSlot;
	}
    
    public Slot oldSlot() {
        if(_shared == null){
            return null;
        }
        return _shared.slot();
    }

	public Object read(ByteArrayBuffer reader) {
		SlotChange change = new SlotChange(reader.readInt());
		change._newSlot = new Slot(reader.readInt(), reader.readInt());
		change.doSetPointer();
		return change;
	}

	public void rollback(LocalObjectContainer container) {
		if (_shared != null) {
			container.reduceFreeOnCommitReferences(_shared);
		}
		if (isFreeOnRollback()) {
			container.free(_newSlot);
		}
		if(isFreePointerOnRollback()){
		    if(DTrace.enabled){
		        DTrace.FREE_POINTER_ON_ROLLBACK.logLength(_key, Const4.POINTER_LENGTH);
		    }
			container.free(_key, Const4.POINTER_LENGTH);
		}
	}

	private final void setBit(int bitPos) {
		_action |= (1 << bitPos);
	}

	public void setPointer(Slot slot) {
		doSetPointer();
		_newSlot = slot;
	}

	public void write(ByteArrayBuffer writer) {
		if (isSetPointer()) {
			writer.writeInt(_key);
			writer.writeInt(_newSlot.address());
			writer.writeInt(_newSlot.length());
		}
	}

	public final void writePointer(LocalTransaction trans) {
		if (isSetPointer()) {
			trans.writePointer(_key, _newSlot);
		}
	}

    public void forFreespace(boolean flag) {
        if(flag){
            setBit(FREESPACE_BIT);
        }
    }
}
