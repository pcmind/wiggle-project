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
package com.db4o.db4ounit.common.filelock;

import java.io.*;

import com.db4o.*;
import com.db4o.db4ounit.common.api.*;
import com.db4o.db4ounit.util.*;
import com.db4o.ext.*;

import db4ounit.*;
import db4ounit.extensions.fixtures.*;
import db4ounit.extensions.util.IOServices.*;

@decaf.Remove
public class DatabaseFileLockedAcrossVMTestCase
	extends TestWithTempFile
	implements OptOutInMemory {
	
	public void testLockedFile() throws IOException{
		ProcessRunner externalVM = JavaServices.startJava(AcquireNativeLock.class.getName(), new String[]{ tempFile() });
		externalVM.checkIfStarted("ready", 3000);
		try {
			Assert.expect(DatabaseFileLockedException.class, new CodeBlock() {
				public void run() throws Throwable {
					EmbeddedObjectContainer objectContainer = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), tempFile());
				}
			});
		} finally {
			externalVM.write("");
			try {
				externalVM.waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	

	public static void main(String[] args) {
		new ConsoleTestRunner(DatabaseFileLockedAcrossVMTestCase.class).run();
	}
}
