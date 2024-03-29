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
package com.db4o.cs.internal.config;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.cs.config.*;
import com.db4o.cs.internal.*;
import com.db4o.ext.*;
import com.db4o.internal.*;

/**
 * @exclude
 */
public class LegacyClientServerFactoryImpl implements LegacyClientServerFactory{

	public ObjectContainer openClient(Configuration config, String hostName,
			int port, String user, String password) throws Db4oIOException,
			OldFormatException, InvalidPasswordException {
		if (user == null || password == null) {
			throw new InvalidPasswordException();
		}
		
		Config4Impl.assertIsNotTainted(config);
		ClientConfiguration clientConfig = Db4oClientServerLegacyConfigurationBridge.asClientConfiguration(config);
		Socket4Adapter networkSocket = new Socket4Adapter(clientConfig.networking().socketFactory(), hostName, port);
		return new ClientObjectContainer(clientConfig, networkSocket, user, password, true);
	}


	public ObjectServer openServer(Configuration config,
			String databaseFileName, int port)
			throws Db4oIOException, IncompatibleFileFormatException,
			OldFormatException, DatabaseFileLockedException,
			DatabaseReadOnlyException {
		LocalObjectContainer container = (LocalObjectContainer)Db4o.openFile(config, databaseFileName);
        if(container == null){
            return null;
        }
		ServerConfiguration serverConfig = Db4oClientServerLegacyConfigurationBridge.asServerConfiguration(config);
        synchronized(container.lock()){
            return new ObjectServerImpl(container, serverConfig, port);
        }
	}

}
