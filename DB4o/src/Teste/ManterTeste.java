package Teste;

import javax.persistence.EntityManager;

import org.ufrj.db4o.EntityManagerWrapper;

import Teste.Entidades.Pagina;
import Teste.Entidades.Termo;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;

public class ManterTeste {

	EntityManager em = new EntityManagerWrapper();
	
	public void listarTermos(){
		
	}
	
	public void persistirTermo(Termo termo){
		
		try{
			em.persist(termo);
			em.flush();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void deletarTermo(Termo termo){
		try{
			em.remove(termo);
			em.flush();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void atualizarTermo(Termo termo){
		try{
			em.merge(termo);
			em.flush();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public Termo recuperarTermo(Integer chave){
		try{
			return em.find(Termo.class, chave);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public Termo recuperarTermoCorreto(Termo termo){
		ObjectSet<Termo> objectSet = ((ObjectContainer)em.getDelegate()).queryByExample(termo);
		
		if(objectSet.size()!=1){
			return null;
		}
		return objectSet.next();
	}
	
	public void persistirPagina(Pagina pagina){
		
		try{
			em.persist(pagina);
			em.flush();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void deletarPagina(Pagina pagina){
		try{
			em.remove(pagina);
			em.flush();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void atualizarPagina(Pagina pagina){
		try{
			em.merge(pagina);
			em.flush();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public Termo recuperarPagina(Integer chave){
		try{
			return em.find(Termo.class, chave);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	
	
}
