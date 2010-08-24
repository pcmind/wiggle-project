package Teste;

import Teste.Entidades.Pagina;
import Teste.Entidades.Termo;


public class TestClient {

	public static void main(String[] args) {
		new TestServer().runServer();
		
		new TestClient().runClient();
	}

	ManterTeste manterTeste = new ManterTeste();
	private void runClient() {
		
		Pagina pagina = new Pagina();
		pagina.setId(1);
		pagina.setChave("Teste 2");
		pagina.setConteudo("blablabla");
		manterTeste.persistirPagina(pagina);
		//System.out.println();
	
		
		/*manterTeste.persistirTermo(termo);
		manterTeste.deletarTermo(termo);
		manterTeste.atualizarTermo(termo);
		manterTeste.recuperarTermo(1);
		 manterTeste.recuperarTermoCorreto(termo);
		
		Pagina pagina = new Pagina();
		manterTeste.persistirPagina(pagina);
		manterTeste.deletarPagina(pagina);
		manterTeste.atualizarPagina(pagina);
		manterTeste.recuperarPagina(1);*/
		
		
		
		
		
				
	}
	
	
}
