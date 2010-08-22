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
package com.db4o.test;

import com.db4o.*;
import com.db4o.ext.*;

/**
 * Runs the Regression test on a memory file
 */
public class RegressionMemoryFile extends Regression {
	
	ObjectContainer con;

	public static void main(String[] args) {
		System.out.println("Memory File Regression Test");
		Db4o.configure().messageLevel(-1);
		new RegressionMemoryFile().run();
	}
	
	public RegressionMemoryFile(){
		configure();
		MemoryFile mf = new MemoryFile();
		con= ExtDb4o.openMemoryFile(mf);
	}
	
	public void completed(){
		con.close();
	}

	public ObjectContainer openContainer() {
		return con;
	}
	
	public void close(ObjectContainer container) {
		// don't close
	}
	
	protected void closeAllButMemoryFile(ObjectContainer container){
		
	}
	
	
}
