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
package com.db4o.db4ounit.jre5.collections;

import com.db4o.activation.*;
import com.db4o.db4ounit.common.ta.*;


/**
 */
@decaf.Ignore
public class OrderItem extends ActivatableImpl {
	private Product _product;
	private int _quantity;
	
	public OrderItem(Product product, int quantity) {
		_product = product;
		_quantity = quantity; 
	}
	
	public Product product() {
		activate(ActivationPurpose.READ);
		return _product;
	}
	
	public int quantity() {
		activate(ActivationPurpose.READ);
		return _quantity;
	}
}
