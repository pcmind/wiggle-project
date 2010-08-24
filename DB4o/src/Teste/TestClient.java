package Teste;

import java.util.List;

import Teste.Entidades.Pagina;



public class TestClient {

	public static void main(String[] args) {
		new TestServer().runServer();
		
		new TestClient().runClient();
	}

	ManterTeste manterTeste = new ManterTeste();
	private void runClient() {
		
	List<Pagina> listaPagina = manterTeste.recuperarPaginaQuery1();
	
	for(Pagina pagina : listaPagina){
		System.out.println(pagina.getChave());
	}
	
	Pagina pagina2 = manterTeste.recuperarpaginaById();
	System.out.println(pagina2.getChave());
		
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
