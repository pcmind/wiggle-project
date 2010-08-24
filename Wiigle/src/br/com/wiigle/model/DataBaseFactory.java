package br.com.wiigle.model;

import br.com.wiigle.model.entity.Termo;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.config.Configuration;

public class DataBaseFactory {

	private static ObjectContainer db = null;
	
	private static ObjectContainer openDatabase(){
		return Db4o.openFile(getConfiguration(), "./BD/desambiguationDB.yap");
	}
	
	private static Configuration getConfiguration(){
		Configuration conf = Db4o.cloneConfiguration();
		conf.objectClass(Termo.class).objectField("chave").indexed(true);
		conf.objectClass(Termo.class).updateDepth(3);
		return conf;
	}
	
	protected synchronized static ObjectContainer getDatabase(){
		if(db==null)
			db = openDatabase();
		
		return db;
	}
	
	protected static Boolean closeDatabase(){
		boolean closed = db.close();
		db = null;
		
		return closed; 
	}
}
