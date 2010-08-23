package org.ufrj.db4o.wrapper;

import java.util.HashMap;
import java.util.Map;

public class From {

	private Map<String, Class<?>> mapaClasses;
	
	
	private Map<String, Class<?>> getMapaClasses(){
		if(mapaClasses == null){
			mapaClasses = new HashMap<String, Class<?>>();
		}
		return mapaClasses;
	}
	
	public Class recuperarClasse(String alias){
		return getMapaClasses().get(alias);
	}
	
	public void adicionarClasse(String alias, Class clazz){
		getMapaClasses().put(alias, clazz);
	}
}
