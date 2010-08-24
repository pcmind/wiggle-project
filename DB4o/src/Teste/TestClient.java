package Teste;

import Teste.Entidades.Termo;


public class TestClient {

	public static void main(String[] args) {
		new TestServer().runServer();
		
		new TestClient().runClient();
	}

	ManterTeste manterTeste = new ManterTeste();
	private void runClient() {
		
		Termo termo = new Termo();
		termo.setChave("RealTroca");
		manterTeste.atualizarTermo(termo);
		
	
		
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
