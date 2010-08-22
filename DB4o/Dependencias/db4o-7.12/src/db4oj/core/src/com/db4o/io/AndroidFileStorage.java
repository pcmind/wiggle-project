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
package com.db4o.io;

import java.io.*;

import com.db4o.io.FileStorage.*;

/**
 * @sharpen.ignore
 */
@decaf.Ignore
public class AndroidFileStorage extends StorageDecorator {

	public AndroidFileStorage(FileStorage storage) {
		super(storage);
	}

	@Override
	protected Bin decorate(BinConfiguration config, Bin bin) {
		return new AndroidBin((FileBin) bin);
	}
	
	private static class AndroidBin extends BinDecorator {

		public AndroidBin(FileBin bin) {
			super(bin);
		}

		@Override
		public void close() {
			// FIXME: This is a temporary quickfix for a bug in Android.
			//        Remove after Android has been fixed.
			try {
				FileBin fileBin = (FileBin)_bin;
				if (!fileBin.isClosed()) {
					fileBin.seek(0);
				}
			} catch (IOException e) {
				// ignore
			}
			super.close();
		}
	}
}
