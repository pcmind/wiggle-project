package org.ufrj.db4o.wrapper;

import java.io.IOException;

import com.db4o.ObjectContainer;
import com.db4o.ObjectServer;
import com.db4o.config.Configuration;
import com.db4o.cs.internal.ClientConnectionEventArgs;
import com.db4o.cs.internal.ObjectServerEvents;
import com.db4o.cs.internal.ServerClosedEventArgs;
import com.db4o.events.Event4;
import com.db4o.events.StringEventArgs;
import com.db4o.ext.ExtObjectServer;
import com.db4o.types.TransientClass;

public class ObjectServerWrapper implements ObjectServerEvents, ObjectServer, ExtObjectServer, Runnable, TransientClass {

	ObjectServer delegate;
	
	public ObjectServerWrapper(ObjectServer objectServer){
		this.delegate = objectServer;
	}
	
	@Override
	public boolean close() {
		return delegate.close();
	}

	@Override
	public ExtObjectServer ext() {
		return delegate.ext();
	}

	@Override
	public void grantAccess(String userName, String password) {
		delegate.grantAccess(userName, password);
		
	}

	@Override
	public ObjectContainer openClient() {
		return delegate.openClient();
	}

	@Override
	public void backup(String path) throws IOException {
		((ExtObjectServer) delegate).backup(path);
		
	}

	@Override
	public int clientCount() {
		return ((ExtObjectServer) delegate).clientCount();
	}

	@Override
	public Configuration configure() {
		// TODO Auto-generated method stub
		return ((ExtObjectServer) delegate).configure();
	}

	@Override
	public ObjectContainer objectContainer() {
		return ((ExtObjectServer) delegate).objectContainer();
	}

	@Override
	public int port() {
		return ((ExtObjectServer) delegate).port();
	}

	@Override
	public void revokeAccess(String userName) {
		((ExtObjectServer) delegate).revokeAccess(userName);
		
	}

	@Override
	public void run() {
		((Runnable) delegate).run();
		
	}

	@Override
	public Event4<ClientConnectionEventArgs> clientConnected() {
		return ((ObjectServerEvents) delegate).clientConnected();
	}

	@Override
	public Event4<StringEventArgs> clientDisconnected() {
		return ((ObjectServerEvents) delegate).clientDisconnected();
	}

	@Override
	public Event4<ServerClosedEventArgs> closed() {
		return ((ObjectServerEvents) delegate).closed();
	}


}
