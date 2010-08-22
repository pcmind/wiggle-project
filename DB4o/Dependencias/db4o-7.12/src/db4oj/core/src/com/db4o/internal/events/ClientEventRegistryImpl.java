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
package com.db4o.internal.events;

import com.db4o.events.*;
import com.db4o.internal.*;

/**
 * @sharpen.partial
 */
public class ClientEventRegistryImpl extends EventRegistryImpl {

	public ClientEventRegistryImpl(InternalObjectContainer container) {
		super(container);
	}

	/**
	 * @sharpen.ignore
	 */
	public Event4 deleted() {
		throw new IllegalArgumentException("delete() events are raised only at server side.");
	}
	
	/**
	 * @sharpen.ignore
	 */
	public Event4 deleting() {
		throw new IllegalArgumentException("deleting() events are raised only at server side.");
	}
}
