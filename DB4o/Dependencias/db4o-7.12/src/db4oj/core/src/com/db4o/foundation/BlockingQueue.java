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
package com.db4o.foundation;

/**
 * @exclude
 */
public class BlockingQueue implements Queue4 {
    
	protected NonblockingQueue _queue = new NonblockingQueue();

	protected Lock4 _lock = new Lock4();
	
	protected boolean _stopped;

	public void add(final Object obj) {
		if(obj == null){
			throw new IllegalArgumentException();
		}
		_lock.run(new Closure4() {
			public Object run() {
				_queue.add(obj);
				_lock.awake();
				return null;
			}
		});
	}

	public boolean hasNext() {
		Boolean hasNext = (Boolean) _lock.run(new Closure4() {
			public Object run() {
				return new Boolean(_queue.hasNext());
			}
		});
		return hasNext.booleanValue();
	}

	public Iterator4 iterator() {
		return (Iterator4) _lock.run(new Closure4() {
			public Object run() {
				return _queue.iterator();
			}
		});
	}

	public Object next() throws BlockingQueueStoppedException {
		return _lock.run(new Closure4() {
			public Object run() {
				while(true){
					if (_queue.hasNext()) {
						return _queue.next();
					}
					if(_stopped) {
						throw new BlockingQueueStoppedException();
					}
					_lock.snooze(Integer.MAX_VALUE);
				}
			}
		});
	}
	
	public void stop(){
		_lock.run(new Closure4() {
			public Object run() {
				_stopped = true;
				_lock.awake();
				return null;
			}
		});
	}

	public Object nextMatching(final Predicate4 condition) {
		return _lock.run(new Closure4() {
			public Object run() {
				return _queue.nextMatching(condition);
			}
		});
	}
}
