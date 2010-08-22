package org.ufrj.db4o;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.ufrj.db4o.exception.OperacaoNaoRealizadaException;
import org.ufrj.db4o.wrapper.EntityClass;
import org.ufrj.db4o.wrapper.EntityField;
import org.ufrj.db4o.wrapper.EntityId;

public class EntityHandler {

	public static EntityClass create(Class clazz) throws OperacaoNaoRealizadaException{
		
		EntityClass entityClass = new EntityClass();
		
		//recupera um possível alias da classe
		Entity annEntity = (Entity) clazz.getAnnotation(Entity.class);
		entityClass.setAlias(annEntity.name());
		
		for(Field field : clazz.getDeclaredFields()){
			
			//verifica a existencia de um identificador
			if(field.getAnnotation(Id.class)!=null){
				entityClass.setEntityId(geraEntityId(field));
			}else{
				
				
				if(field.getAnnotation(Transient.class)!=null){
					throw new OperacaoNaoRealizadaException("@Transient não é permitido no db4o.");
				}
			
				entityClass.adicionarField(geraEntityField(field));
			}
		}
		
		return entityClass;
	}
	
	private static EntityId geraEntityId(Field field){
		
		EntityId entityId = new EntityId();
		entityId.setClazz(field.getClass());
		if(field.getAnnotation(GeneratedValue.class)!=null){
			entityId.setAutoIncrement(true);
		}
		return entityId;
	}
	
	private static EntityField geraEntityField(Field field){
		EntityField entityField = new EntityField();
		
		if(field.getClass().equals(List.class) || field.getClass().equals(ArrayList.class) ||
				field.getClass().equals(Set.class) || field.getClass().equals(HashSet.class)){
			
			
			
		}
		
		Annotation annField = field.getAnnotation(OneToOne.class);
		if(annField!=null){
			
		}
		annField = field.getAnnotation(OneToMany.class);
		annField = field.getAnnotation(ManyToOne.class);
		annField = field.getAnnotation(ManyToMany.class);
		
		
		return entityField;
	}
	
	
}
