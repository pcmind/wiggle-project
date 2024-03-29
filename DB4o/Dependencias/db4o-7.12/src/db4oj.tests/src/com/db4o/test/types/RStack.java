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
package com.db4o.test.types;

import java.util.*;

import com.db4o.*;
import com.db4o.test.*;

public class RStack extends RVector{

	public Object newInstance(){
		return new Stack();
	}

	public void specific(ObjectContainer con, int step){
		super.specific(con,step);
		TEntry entry = new TEntry().lastElement();
		Stack stack = new Stack();
		if(step > 0){
			stack.addElement(entry.key);
			ObjectSet set = con.queryByExample(stack);
			if(set.size() != step){
				Regression.addError("Stack member query not found");
			}else{
				Stack res = (Stack)set.next();
				if(! (stack.pop().equals(new TEntry().lastElement().key))){
					Regression.addError("Stack order changed.");
				}
			}
		}
	}
}
