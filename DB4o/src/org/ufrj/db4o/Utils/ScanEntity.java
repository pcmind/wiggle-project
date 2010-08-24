package org.ufrj.db4o.Utils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;

public class ScanEntity {

	
	public static Set<Class<?>> findAll(ClassLoader classLoader){
		
		
		//recupera todas as classes da aplicação
		Set<Class<?>> classes = new HashSet<Class<?>>();
		
		for(Class clazz :  ScanClass.findAll(classLoader, 
									Collections.EMPTY_SET, Collections.EMPTY_SET)){
		
			if(clazz.getAnnotation(Entity.class)!=null || clazz.getAnnotation(MappedSuperclass.class)!=null || clazz.getAnnotation(Embeddable.class)!=null){
				classes.add(clazz);
			}
		}
		return classes;
	}
}
