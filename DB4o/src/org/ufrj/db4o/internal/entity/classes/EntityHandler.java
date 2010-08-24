package org.ufrj.db4o.internal.entity.classes;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.hibernate.validator.NotEmpty;
import org.hibernate.validator.NotNull;
import org.ufrj.db4o.Utils.GenericTypeInfo;
import org.ufrj.db4o.exception.OperacaoNaoRealizadaException;
import org.ufrj.db4o.internal.entity.NamedQuery;

public class EntityHandler {

	public static Class[] listaAnotacoesAssociacao = {OneToOne.class, OneToMany.class, ManyToOne.class, ManyToMany.class};
	
	public static EntityClass create(Class clazz) throws OperacaoNaoRealizadaException{
		
		EntityClass entityClass = new EntityClass();
		entityClass.setClazz(clazz);
		
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
		entityId.setClazz(GenericTypeInfo.extractType(field));
		entityId.setNomeAtributo(field.getName());
		if(field.getAnnotation(GeneratedValue.class)!=null){
			entityId.setAutoIncrement(true);
		}
		return entityId;
	}
	
	private static EntityField geraEntityField(Field field){
		
		EntityField entityField = new EntityField();
		entityField.setFieldName(field.getName());
		
		if(field.getType().equals(List.class) || field.getType().equals(ArrayList.class) ||
				field.getType().equals(Set.class) || field.getType().equals(HashSet.class)){
			
			entityField.setClazz(GenericTypeInfo.extractTypeCollection(field));
			entityField.setCollection(true);
			
			
		}else{
			entityField.setClazz(GenericTypeInfo.extractType(field));
			Class entityClazz = entityField.getClazz();
			
			//tipos do java.lang são sempre carregados por um entity manager
			if(entityClazz.equals(Integer.class) || entityClazz.equals(Float.class) || entityClazz.equals(String.class)
					|| entityClazz.equals(int.class) || entityClazz.equals(float.class) || entityClazz.equals(long.class)
					|| entityClazz.equals(boolean.class) || entityClazz.equals(Boolean.class)){
				entityField.setFetchLazy(false);
				
			}
		}
		
		if(field.getAnnotation(NotNull.class)!=null || field.getAnnotation(NotEmpty.class)!=null){
			entityField.setNotNull(true);
		}
		
		Annotation annAssociacao = null;
		for(Class annotationClass : listaAnotacoesAssociacao){
			annAssociacao = field.getAnnotation(annotationClass);
			if(annAssociacao!=null){
				try {
					Method metodo = annotationClass.getMethod("fetch", (Class[])null );
					FetchType fetch = (FetchType) metodo.invoke(annAssociacao,(Object[]) null);
					
					//o default é lazy
					if(fetch.equals(FetchType.EAGER)){
						entityField.setFetchLazy(false);
					}
					
					metodo = annotationClass.getMethod("cascade", (Class[])null );
					CascadeType[] listaCascade = (CascadeType[]) metodo.invoke(annAssociacao,(Object[]) null);
					for(CascadeType cascade : listaCascade){
						if(cascade.equals(CascadeType.ALL)){
							entityField.setCascadeDelete(true);
							entityField.setCascadeInsert(true);
							entityField.setCascadeUpdate(true);
							break;
						
						}else if(cascade.equals(CascadeType.MERGE)){
							entityField.setCascadeUpdate(true);
						}else if(cascade.equals(CascadeType.PERSIST)){
							entityField.setCascadeInsert(true);
						}else if(cascade.equals(CascadeType.REMOVE)){
							entityField.setCascadeDelete(true);
						}
					}
					
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;// só pode existir uma anotação desse tipo, caso tenha mais de uma ela será ignorada.
			}
		}
		
		return entityField;
	}
	
	
	public static List<NamedQuery> namedQueries(Class clazz){
		
		List<NamedQuery> listaNamedQuery = new ArrayList<NamedQuery>();
		if(clazz.getAnnotation(javax.persistence.NamedQuery.class)!=null){
			NamedQuery namedQuery = new NamedQuery();
			javax.persistence.NamedQuery query = (javax.persistence.NamedQuery) clazz.getAnnotation(javax.persistence.NamedQuery.class);
			namedQuery.setName(query.name());
			namedQuery.setQuery(query.query());
			listaNamedQuery.add(namedQuery);
			
		}else if(clazz.getAnnotation(NamedQueries.class)!=null){
			NamedQueries namedQueries = (NamedQueries) clazz.getAnnotation(javax.persistence.NamedQueries.class);
			for(javax.persistence.NamedQuery query: namedQueries.value()){
				NamedQuery namedQuery = new NamedQuery();
				namedQuery.setName(query.name());
				namedQuery.setQuery(query.query());
				listaNamedQuery.add(namedQuery);
			}
		}
			
		return listaNamedQuery;	
		
	}
	
}
