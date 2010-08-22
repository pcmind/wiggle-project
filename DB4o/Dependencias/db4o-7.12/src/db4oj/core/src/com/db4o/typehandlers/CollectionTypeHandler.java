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
package com.db4o.typehandlers;import java.util.*;import com.db4o.ext.*;import com.db4o.foundation.*;import com.db4o.internal.*;import com.db4o.internal.delete.*;import com.db4o.internal.handlers.*;import com.db4o.internal.marshall.*;import com.db4o.marshall.*;/** * TypeHandler for Collections. *  * On the .NET side, usage is restricted to instances of IList. *  * @sharpen.partial */@decaf.Ignore(decaf.Platform.JDK11)public class CollectionTypeHandler implements ReferenceTypeHandler, CascadingTypeHandler,		VariableLengthTypeHandler, QueryableTypeHandler {	public PreparedComparison prepareComparison(Context context, Object obj) {		// TODO Auto-generated method stub		return null;	}	public void write(WriteContext context, Object obj) {		Collection collection = (Collection) obj;		TypeHandler4 elementHandler = detectElementTypeHandler(container(context), collection);		writeElementClassMetadataId(context, elementHandler);		writeElementCount(context, collection);		writeElements(context, collection, elementHandler);	}	public void activate(ReferenceActivationContext context) {		Collection collection = (Collection) ((UnmarshallingContext) context).persistentObject();		clearCollection(collection);		TypeHandler4 elementHandler = readElementTypeHandler(context, context);		int elementCount = context.readInt();		for (int i = 0; i < elementCount; i++) {			Object element = context.readObject(elementHandler);			addToCollection(collection, element);		}	}	private void writeElementCount(WriteContext context, Collection collection) {		context.writeInt(collection.size());	}	private void writeElements(WriteContext context, Collection collection, TypeHandler4 elementHandler) {		final Iterator elements = collection.iterator();		while (elements.hasNext()) {			context.writeObject(elementHandler, elements.next());		}	}	private ObjectContainerBase container(Context context) {		return ((InternalObjectContainer) context.objectContainer())				.container();	}	public void delete(final DeleteContext context) throws Db4oIOException {		if (!context.cascadeDelete()) {			return;		}		TypeHandler4 handler = readElementTypeHandler(context, context);		int elementCount = context.readInt();		for (int i = elementCount; i > 0; i--) {			handler.delete(context);		}	}	public void defragment(DefragmentContext context) {		TypeHandler4 handler = readElementTypeHandler(context, context);		int elementCount = context.readInt();		for (int i = 0; i < elementCount; i++) {			handler.defragment(context);		}	}	public final void cascadeActivation(ActivationContext context) {		Iterator all = ((Collection) context.targetObject()).iterator();		while (all.hasNext()) {			context.cascadeActivationToChild(all.next());		}	}	public TypeHandler4 readCandidateHandler(QueryingReadContext context) {		return this;	}    public void collectIDs(final QueryingReadContext context) {        TypeHandler4 elementHandler = readElementTypeHandler(context, context);        int elementCount = context.readInt();        for (int i = 0; i < elementCount; i++) {            context.readId(elementHandler);        }    }	private void writeElementClassMetadataId(WriteContext context, TypeHandler4 elementHandler) {		context.writeInt(0);	}	private TypeHandler4 readElementTypeHandler(ReadBuffer buffer, Context context) {		buffer.readInt();		return (TypeHandler4) container(context).handlers().openTypeHandler();	}	private TypeHandler4 detectElementTypeHandler(InternalObjectContainer container, Collection collection) {		return (TypeHandler4) container.handlers().openTypeHandler();	}	/**	 * @sharpen.ignore	 */	private void addToCollection(Collection collection, Object element) {		collection.add(element);	}	/**	 * @sharpen.ignore	 */	private void clearCollection(Collection collection) {		collection.clear();	}	public boolean descendsIntoMembers() {		return true;    }}