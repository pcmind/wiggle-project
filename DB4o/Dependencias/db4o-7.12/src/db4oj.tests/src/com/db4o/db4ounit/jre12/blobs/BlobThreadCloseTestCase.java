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
package com.db4o.db4ounit.jre12.blobs;

import java.io.*;

import com.db4o.ext.*;
import com.db4o.foundation.io.*;
import com.db4o.types.*;

import db4ounit.extensions.*;
import db4ounit.extensions.util.*;

/**
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class BlobThreadCloseTestCase extends Db4oClientServerTestCase {

	public static void main(String[] args) {
		new BlobThreadCloseTestCase().runNetworking();
	}

	private static final String TEST_FILE = "test.db4o";
	
	private static class Data {
		private Blob _blob;

		public Data() {
			_blob = null;
		}

		public Blob blob() {
			return _blob;
		}
	}

	protected void db4oTearDownAfterClean() throws Exception {
		File4.delete(TEST_FILE);
		IOUtil.deleteDir("blobs");
	}

	/**
	 * @deprecated using deprecated api
	 */
	public void test() throws Exception {
		if (isEmbedded()) {
			return;
		}
		((ExtClient) db()).switchToFile(TEST_FILE);
		store(new Data());

		Data data = (Data) retrieveOnlyInstance(Data.class);
		data.blob().readFrom(
				new File(BlobThreadCloseTestCase.class.getResource(
						"BlobThreadCloseTestCase.class").getFile()));
		while (data.blob().getStatus() > Status.COMPLETED) {
			Thread.sleep(50);
		}
	}
}