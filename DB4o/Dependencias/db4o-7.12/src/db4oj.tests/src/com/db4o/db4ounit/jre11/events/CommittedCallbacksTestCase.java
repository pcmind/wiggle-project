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
package com.db4o.db4ounit.jre11.events;

import com.db4o.config.*;
import com.db4o.events.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class CommittedCallbacksTestCase extends AbstractDb4oTestCase {
	
	public static void main(String[] args) {
		new CommittedCallbacksTestCase().runAll();
	}
	
	private static final ObjectInfo[] NONE = new ObjectInfo[0];
	
	public static final class Item {
		public int id;
		
		public Item() {
		}
		
		public Item(int id_) {
			id = id_;
		}
		
		public String toString() {
			return "Item(" + id + ")";
		}
	}

	private EventRecorder _eventRecorder;	
	
	protected void configure(Configuration config) {
		config.objectClass(Item.class).generateUUIDs(true);
		indexField(config, Item.class, "id");
	}
	
	protected void store() throws Exception {
		for (int i=0; i<3; ++i) {
			store(new Item(i));
		}
	}
	
	protected void db4oSetupAfterStore() throws Exception {
		_eventRecorder = new EventRecorder(db().lock());
		committed().addListener(_eventRecorder);
	}
	
	protected void db4oTearDownBeforeClean() throws Exception {
		committed().removeListener(_eventRecorder);
	}
	
	public void testLocalTransactionIsAvailableToEventListener() {
		if (isMultiSession()) {
			return;
		}
		
		final Transaction transaction = stream().transaction();
		final ObjectByRef objectByRef = new ObjectByRef();
		eventRegistry().committed().addListener(new EventListener4() {
			public void onEvent(Event4 e, EventArgs args) {
				objectByRef.value = ((CommitEventArgs)args).transaction();
			}
		});
		db().commit();
		Assert.areSame(transaction, objectByRef.value);
	}
	
	public void testCommittedAdded() {
		Item item4 = new Item(4);
		Item item5 = new Item(5);
		db().store(item4);
		db().store(item5);
		
		ObjectInfo info4 = getInfo(4);
		ObjectInfo info5 = getInfo(5);
		
		assertNoEvents();
		
		db().commit();
		
		assertCommittedEvent(new ObjectInfo[] { info4, info5 }, NONE, NONE);
	}
	
	public void testCommittedAddedDeleted() {
		Item item4 = new Item(4);
		Item item1 = getItem(1);
		Item item2 = getItem(2);
		
		ObjectInfo info1 = getInfo(1);
		ObjectInfo info2 = getInfo(2);
		
		db().store(item4);
		db().delete(item1);
		db().delete(item2);
		
		ObjectInfo info4 = getInfo(4);
		
		assertNoEvents();
		
		db().commit();
		assertCommittedEvent(new ObjectInfo[] { info4 }, new ObjectInfo[] { info1, info2 }, NONE);
	}
	
	public void testCommittedAddedUpdatedDeleted() {
		Item item1 = getItem(1);
		Item item2 = getItem(2);
		
		ObjectInfo info1 = getInfo(1);
		ObjectInfo info2 = getInfo(2);
		
		Item item4 = new Item(4);
		db().store(item4);
		db().store(item2);
		db().delete(item1);
		
		ObjectInfo info4 = getInfo(4);
		
		assertNoEvents();
		
		db().commit();
		assertCommittedEvent(new ObjectInfo[] { info4 }, new ObjectInfo[] { info1 }, new ObjectInfo[] { info2 });
	}
	
	public void testCommittedDeleted(){
		Item item1 = getItem(1);
		ObjectInfo info1 = getInfo(1);
		
		assertNoEvents();
		
		db().delete(item1);
		
		db().commit();
		
		assertCommittedEvent(NONE, new ObjectInfo[] { info1 }, NONE);
	}
	
	public void testObjectSetTwiceShouldStillAppearAsAdded() {
		final Item item4 = new Item(4);
		db().store(item4);
		db().store(item4);
		
		ObjectInfo info4 = getInfo(4);
		
		db().commit();
		assertCommittedEvent(new ObjectInfo[] { info4 }, NONE, NONE);
	}
	
	public void testDeleteUncommitted(){
		final Item item4 = new Item(4);
		db().store(item4);
		assertNoEvents();
		db().delete(item4);
		db().commit();
		assertCommittedEvent(NONE, NONE, NONE);
	}
	
	private Item getItem(int id) {
		final Query query = newQuery(Item.class);
		query.descend("id").constrain(new Integer(id));
		return (Item)query.execute().next();
	}
	
	private ObjectInfo getInfo(int itemId) {
		Item item = getItem(itemId);
		return new FrozenObjectInfo(trans(), trans().referenceForObject(item), false);
	}

	private void assertCommittedEvent(
			final ObjectInfo[] expectedAdded,
			final ObjectInfo[] expectedDeleted,
			final ObjectInfo[] expectedUpdated) {
		
		EventAssert.assertCommitEvent(_eventRecorder, committed(), expectedAdded, expectedDeleted, expectedUpdated);
	}

	private void assertNoEvents() {
		EventAssert.assertNoEvents(_eventRecorder);
	}

	private Event4 committed() {
		return eventRegistry().committed();
	}
}
