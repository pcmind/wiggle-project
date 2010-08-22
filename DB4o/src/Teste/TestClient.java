package Teste;

import java.util.Date;

import org.ufrj.db4o.exception.InvalidLoginException;
import org.ufrj.db4o.wrapper.DB4oServerWrapper;

import Teste.Entidades.Termo;
import Teste.Entidades.TermoIndexado;

import com.db4o.ObjectContainer;

public class TestClient {

	public static void main(String[] args) {
		new TestServer().runServer();
		
		new TestClient().runClient();
	}

	private void runClient() {
		
		ObjectContainer container = null;
		try {
			container = DB4oServerWrapper.openClient();
		} catch (InvalidLoginException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Termo termo = new Termo();
		termo.setChave("Teste");
		termo.setDataExpiracao(new Date());
		try{
		for(Termo termo2 : container.query(Termo.class)){
			
			System.out.println(termo2.getChave() + " " + termo.getDataExpiracao());
		}
		//container.store(termo);
		//container.commit();
		container.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	
}
