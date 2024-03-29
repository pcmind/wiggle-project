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
package com.db4o.db4ounit.common.api;

import static com.db4o.foundation.Environments.my;

import com.db4o.config.*;
import com.db4o.internal.config.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class EnvironmentConfigurationTestCase extends AbstractInMemoryDb4oTestCase {
	
	public void test() {
		container().withEnvironment(new Runnable() { public void run() {
			
			Assert.areSame(_service, my(ServiceInterface.class));
			
		}});
	}
	
	public interface ServiceInterface {
	}
	
	private ServiceInterface _service = new ServiceInterface() {};
	
	@Override
	protected void configure(Configuration legacy) throws Exception {
		super.configure(legacy);
		
		final CommonConfiguration common = Db4oLegacyConfigurationBridge.asCommonConfiguration(legacy);
		common.environment().add(_service);
	}

}
