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
package com.db4o.db4ounit.common.exceptions;

import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.internal.*;
import com.db4o.io.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class InvalidSlotExceptionTestCase extends AbstractDb4oTestCase {
	
	private static final int INVALID_ID = 3;
	
	private static final int OUT_OF_MEMORY_ID = 4;

	public static void main(String[] args) {
		new InvalidSlotExceptionTestCase().runAll();
	}
	
	
	protected void configure(Configuration config) throws Exception {
		config.storage(new MockStorage());
	}
	
	public void testInvalidSlotException() throws Exception {
		Assert.expect(Db4oRecoverableException.class, new CodeBlock(){
			public void run() throws Throwable {
				db().getByID(INVALID_ID);		
			}
		});
		Assert.isFalse(db().isClosed());
	}
	
	public void testDbNotClosedOnOutOfMemory() {
		Assert.expect(Db4oRecoverableException.class, OutOfMemoryError.class, new CodeBlock(){
			public void run() throws Throwable {
				db().getByID(OUT_OF_MEMORY_ID);
			}
		});
		Assert.isFalse(db().isClosed());
	}

	public static class A{
		
		A _a;
		
		public A(A a) {
			this._a = a;
		}
	}
	
	public static class MockStorage extends StorageDecorator {
				
		public MockStorage(){
			super(new FileStorage());
		}

		@Override
		protected Bin decorate(BinConfiguration config, Bin bin) {
			return new MockBin(bin);
		}
		
		private static class MockBin extends BinDecorator {
			private boolean _deliverInvalidSlot;

			public MockBin(Bin bin) {
				super(bin);
			}
			
			public int read(long pos, byte[] bytes, int length) throws Db4oIOException {
				seek(pos);
				if(_deliverInvalidSlot){
					ByteArrayBuffer buffer = new ByteArrayBuffer(Const4.POINTER_LENGTH);
					buffer.writeInt(1);
					buffer.writeInt(Integer.MAX_VALUE);
					System.arraycopy(buffer._buffer, 0, bytes, 0, Const4.POINTER_LENGTH);
					return length;
				}
				return super.read(pos, bytes, length);
			}

			private void seek(long pos) throws Db4oIOException {
				if(pos == OUT_OF_MEMORY_ID){
					throw new OutOfMemoryError();
				}
				if(pos == INVALID_ID){
					_deliverInvalidSlot = true;
					return;
				}
				_deliverInvalidSlot = false;
			}
		}
	}
}
