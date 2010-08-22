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
package db4ounit.extensions;

import com.db4o.config.*;
import com.db4o.io.*;

import db4ounit.extensions.fixtures.*;

public class AbstractInMemoryDb4oTestCase extends AbstractDb4oTestCase implements OptOutMultiSession {
	
	@Override
	protected void configure(Configuration config) throws Exception {
		config.storage(new MemoryStorage());
	}

}
