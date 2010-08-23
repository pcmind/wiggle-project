package Teste;

import javax.persistence.EntityManager;

import org.ufrj.db4o.EntityManagerWrapper;

import Teste.Entidades.Termo;

public class ManterTeste {

	EntityManager em = new EntityManagerWrapper();
	
	public void listarTermos(){
		
	}
	
	public void persistirTermo(Termo termo){
		em.persist(termo);
	}
	
}
