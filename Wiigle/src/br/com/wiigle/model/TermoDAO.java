package br.com.wiigle.model;

import br.com.wiigle.model.entity.Termo;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Query;

public class TermoDAO {
	public static synchronized Termo recuperarTermo(String chave){
		ObjectContainer db = DataBaseFactory.getDatabase();
		
		try{
			Query query = db.query();
			query.constrain(Termo.class);
			query.descend("chave").constrain(chave);
			ObjectSet<Termo> resultados = query.execute();
			
			if(resultados.hasNext())
				return resultados.next();
			else
				return null;
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static synchronized void insertOrUpdateTermo(Termo termo){
		ObjectContainer db = DataBaseFactory.getDatabase();
		
		try{
			db.store(termo);
			db.commit();
		}catch(Exception e){
			db.rollback();
			e.printStackTrace();
		}
	}
	
}
