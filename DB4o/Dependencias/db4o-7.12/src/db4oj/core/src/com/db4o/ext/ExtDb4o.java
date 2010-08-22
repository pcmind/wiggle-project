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
package com.db4o.ext;

import com.db4o.*;
import com.db4o.config.*;

/**
 * extended factory class with static methods to open special db4o sessions.
 * 
 * @sharpen.rename ExtDb4oFactory
 */
public class ExtDb4o extends Db4o {
   /**
     * Operates just like {@link ExtDb4o#openMemoryFile(Configuration, MemoryFile)}, but uses
     * the global db4o {@link Configuration Configuration} context.
     * 
     * opens an {@link ObjectContainer} for in-memory use .
	 * <br><br>In-memory ObjectContainers are useful for maximum performance
	 * on small databases, for swapping objects or for storing db4o format data
	 * to other media or other databases.<br><br>Be aware of the danger of running
	 * into OutOfMemory problems or complete loss of all data, in case of hardware
	 * or JVM failures.<br><br>
     * @param memoryFile a {@link MemoryFile MemoryFile} 
     * to store the raw byte data.
	 * @return an open {@link com.db4o.ObjectContainer ObjectContainer}
     * @see MemoryFile
     * 
     * @deprecated Use {@link #openMemoryFile(Configuration, MemoryFile)} instead.
	 */
	public static final ObjectContainer openMemoryFile(MemoryFile memoryFile) {
		return openMemoryFile1(Db4o.newConfiguration(),memoryFile);
	}

	/**
     * opens an {@link ObjectContainer} for in-memory use .
	 * <br><br>In-memory ObjectContainers are useful for maximum performance
	 * on small databases, for swapping objects or for storing db4o format data
	 * to other media or other databases.<br><br>Be aware of the danger of running
	 * into OutOfMemory problems or complete loss of all data, in case of hardware
	 * or JVM failures.<br><br>
	 * @param config a custom {@link Configuration Configuration} instance to be obtained via {@link Db4o#newConfiguration()}
     * @param memoryFile a {@link MemoryFile MemoryFile} 
     * to store the raw byte data.
	 * @return an open {@link com.db4o.ObjectContainer ObjectContainer}
     * @see MemoryFile
	 */
	public static final ObjectContainer openMemoryFile(Configuration config,MemoryFile memoryFile) {
		return openMemoryFile1(config,memoryFile);
	}
}
