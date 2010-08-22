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
package com.db4o.defragment;

import java.io.*;

/**
 * The ID mapping used internally during a defragmentation run.
 * 
 * @see Defragment
 */
public interface ContextIDMapping {

	/**
	 * Returns a previously registered mapping ID for the given ID if it exists.
	 * If lenient mode is set to true, will provide the mapping ID for the next
	 * smaller original ID a mapping exists for. Otherwise returns 0.
	 * 
	 * @param origID The original ID
	 * @param lenient If true, lenient mode will be used for lookup, strict mode otherwise.
	 * @return The mapping ID for the given original ID or 0, if none has been registered.
	 */
	int mappedID(int origID, boolean lenient);

	/**
	 * Registers a mapping for the given IDs.
	 * 
	 * @param origID The original ID
	 * @param mappedID The ID to be mapped to the original ID.
	 * @param isClassID true if the given original ID specifies a class slot, false otherwise.
	 */
	void mapIDs(int origID, int mappedID, boolean isClassID);

	/**
	 * Prepares the mapping for use.
	 */
	void open() throws IOException;	
	
	/**
	 * Shuts down the mapping after use.
	 */
	void close();
}