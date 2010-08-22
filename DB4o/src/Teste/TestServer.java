package Teste;

import org.ufrj.db4o.wrapper.DB4oServerWrapper;

import com.db4o.ObjectServer;
import com.db4o.ext.InvalidPasswordException;

public class TestServer {
	
	public void runServer(){
		//System.out.println(TestServer.class.getName());
		//Set<Class<?>> classes = ScanClass.findAll(Thread.currentThread().getContextClassLoader(), Collections.EMPTY_SET, Collections.EMPTY_SET);
		ObjectServer objectServer = DB4oServerWrapper.openServer("BaseTeste/BD.yap", 5050);
		
	}
}
