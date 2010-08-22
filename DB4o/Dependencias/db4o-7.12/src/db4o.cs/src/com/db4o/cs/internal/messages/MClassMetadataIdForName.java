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
package com.db4o.cs.internal.messages;

import com.db4o.ext.*;
import com.db4o.internal.*;


/**
 * @exclude
 */
public class MClassMetadataIdForName extends MsgD implements MessageWithResponse {
    
    public final Msg replyFromServer() {
        String name = readString();
        ObjectContainerBase stream = stream();
        Transaction trans = stream.systemTransaction();
        try {
            synchronized (streamLock()) {
                int id = stream.classMetadataIdForName(name);
                return Msg.CLASS_ID.getWriterForInt(trans, id);
            }
        } 
        catch (Db4oException e) {
            // TODO: send the exception to the client
        } 
        return Msg.FAILED;
    }


}
