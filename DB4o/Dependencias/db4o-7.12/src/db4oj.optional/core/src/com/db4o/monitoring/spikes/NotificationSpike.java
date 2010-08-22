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
package com.db4o.monitoring.spikes;

import javax.management.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.foundation.*;
import com.db4o.io.*;
import com.db4o.monitoring.*;
import com.db4o.query.*;
import com.db4o.query.Query;

@decaf.Ignore
public class NotificationSpike {
	
	public static class Item {
		
		public Item(String id) {
			_id = id;
		}

		private String _id;
	}

	/**
	 * @param args
	 * @throws JMException 
	 * @throws MalformedObjectNameException 
	 */
	public static void main(String[] args) throws MalformedObjectNameException, JMException {
		
		final EmbeddedConfiguration configuration = Db4oEmbedded.newConfiguration();
		configuration.file().storage(new MemoryStorage());
		configuration.common().add(new QueryMonitoringSupport());
		
		final EmbeddedObjectContainer container = Db4oEmbedded.openFile(configuration, "db4o");
		try {
			container.store(new Item("foo"));
			
			Cool.loopWithTimeout(60000, new ConditionalBlock() {
				
				public boolean run() {

					final Query query = container.query();
					query.constrain(Item.class);
					query.descend("_id").constrain("foo");
					query.execute().toArray();
					
					Cool.sleepIgnoringInterruption(200);
					
					container.query(new Predicate<Item>() {
						@Override
						public boolean match(Item candidate) {
							return candidate._id.toLowerCase().equals("FOO");
						}});
					
					return true;
				}
			});
				
			
		} finally {
			container.close();
		}
	}

}
