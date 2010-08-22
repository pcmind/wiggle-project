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
package com.db4o.db4ounit.common.cs;

import com.db4o.*;
import com.db4o.cs.*;
import com.db4o.cs.config.*;
import com.db4o.cs.internal.*;
import com.db4o.db4ounit.common.api.*;
import com.db4o.events.*;
import com.db4o.foundation.*;
import com.db4o.reflect.*;

import db4ounit.*;

public class PrefetchIDCountTestCase extends TestWithTempFile {

	public static void main(String[] args) {
		new ConsoleTestRunner(PrefetchIDCountTestCase.class).run();
	}
	
	private static final int PREFETCH_ID_COUNT = 100;
	private static final String USER = "db4o";
	private static final String PASSWORD = "db4o";
	
	public static class Item {
	}

	public void test() throws Exception {
		final ObjectServerImpl server = (ObjectServerImpl) Db4oClientServer.openServer(tempFile(), Db4oClientServer.ARBITRARY_PORT);
		
		final Lock4 lock = new Lock4();
		server.clientDisconnected().addListener(new EventListener4<StringEventArgs>() { public void onEvent(Event4<StringEventArgs> e, StringEventArgs args) {
			lock.run(new Closure4() { public Object run() {
				lock.awake();
				return null;
			}});
			
		}});
		
		server.grantAccess(USER, PASSWORD);
		final ObjectContainer client = openClient(server.port());
		client.store(new Item());		
		
		final ServerMessageDispatcherImpl msgDispatcher = firstMessageDispatcherFor(server);
		
		lock.run(new Closure4() { public Object run() {
			client.close();
			try {
				lock.snooze(100000);
				
				ReflectClass reflector = server.objectContainer().ext().reflector().forClass(ServerMessageDispatcherImpl.class);
				ReflectField prefetchedIdsField = reflector.getDeclaredField("_prefetchedIDs");
				Assert.isNull(prefetchedIdsField.get(msgDispatcher));
			} catch (Exception e) {
			}
			
			return null;
		}});		
		
		server.close();
	}

	private ServerMessageDispatcherImpl firstMessageDispatcherFor(final ObjectServerImpl server) {
		Iterator4 dispatchers = server.iterateDispatchers();
	
		Assert.isTrue(dispatchers.moveNext());
		final ServerMessageDispatcherImpl msgDispatcher = (ServerMessageDispatcherImpl) dispatchers.current();
		
		return msgDispatcher;
	}

	private ObjectContainer openClient(int port) {
		ClientConfiguration config = Db4oClientServer.newClientConfiguration();
		config.prefetchIDCount(PREFETCH_ID_COUNT);
		return Db4oClientServer.openClient(config, "localhost", port, USER, PASSWORD);
	}	
}
