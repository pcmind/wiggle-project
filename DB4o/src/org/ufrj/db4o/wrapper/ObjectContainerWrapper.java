package org.ufrj.db4o.wrapper;

import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.ufrj.db4o.exception.OperacaoNaoRealizadaException;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.ext.DatabaseClosedException;
import com.db4o.ext.DatabaseReadOnlyException;
import com.db4o.ext.Db4oIOException;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.query.Predicate;
import com.db4o.query.Query;
import com.db4o.query.QueryComparator;

public class ObjectContainerWrapper implements ObjectContainer{

	
	private ObjectContainer delegate;
	private Map<String, EntityClass> mapaEntidades;
	private Map<String, EntityQuery> mapaNamedQuery;
	
	public ObjectContainerWrapper(ObjectContainer objectContainer, Map<String, EntityClass> mapaEntidades, Map<String, EntityQuery> mapaNamedquery){
		this.delegate = objectContainer;
		this.mapaEntidades = mapaEntidades;
		this.mapaNamedQuery = new HashMap<String, EntityQuery>(mapaNamedquery);
	}

	@Override
	public void activate(Object obj, int depth) throws Db4oIOException,
			DatabaseClosedException {
		delegate.activate(obj, depth);
		
	}

	@Override
	public boolean close() throws Db4oIOException {
		return delegate.close();
	}

	@Override
	public void commit() throws Db4oIOException, DatabaseClosedException,
			DatabaseReadOnlyException {
		delegate.commit();
		
	}

	@Override
	public void deactivate(Object obj, int depth)
			throws DatabaseClosedException {
		delegate.deactivate(obj, depth);
		
	}

	@Override
	public void delete(Object obj) throws Db4oIOException,
			DatabaseClosedException, DatabaseReadOnlyException {
		
		if(mapaEntidades.get(obj.getClass().getSimpleName())==null){
			throw new Db4oIOException("Esta classe não é uma entidade");
		}
		delegate.delete(obj);
		
	}

	@Override
	public ExtObjectContainer ext() {
		return delegate.ext();
	}

	@Deprecated
	@Override
	public <T> ObjectSet<T> get(Object template) throws Db4oIOException,
			DatabaseClosedException {
		return delegate.get(template);
	}

	@Override
	public Query query() throws DatabaseClosedException {
		return delegate.query();
	}

	@Override
	public <TargetType> ObjectSet<TargetType> query(Class<TargetType> clazz)
			throws Db4oIOException, DatabaseClosedException {
		return delegate.query(clazz);
	}

	@Override
	public <TargetType> ObjectSet<TargetType> query(
			Predicate<TargetType> predicate) throws Db4oIOException,
			DatabaseClosedException {
		return delegate.query(predicate);
	}

	@Override
	public <TargetType> ObjectSet<TargetType> query(
			Predicate<TargetType> predicate,
			QueryComparator<TargetType> comparator) throws Db4oIOException,
			DatabaseClosedException {
		return delegate.query(predicate, comparator);
	}

	@Override
	public <TargetType> ObjectSet<TargetType> query(
			Predicate<TargetType> predicate, Comparator<TargetType> comparator)
			throws Db4oIOException, DatabaseClosedException {
		return delegate.query(predicate, comparator);
	}

	@Override
	public <T> ObjectSet<T> queryByExample(Object template)
			throws Db4oIOException, DatabaseClosedException {
		return delegate.queryByExample(template);
	}

	@Override
	public void rollback() throws Db4oIOException, DatabaseClosedException,
			DatabaseReadOnlyException {
		delegate.rollback();
		
	}

	@Deprecated
	@Override
	public void set(Object obj) throws DatabaseClosedException,
			DatabaseReadOnlyException {
		delegate.set(obj);
	}

	@Override
	public void store(Object obj) throws DatabaseClosedException,
			DatabaseReadOnlyException {
		delegate.store(obj);
		
	}
	
	public void store(Object obj, boolean newObject) throws DatabaseClosedException, OperacaoNaoRealizadaException{
		EntityClass entityClass = mapaEntidades.get(obj.getClass().getSimpleName());
		
		if(entityClass==null){
			throw new OperacaoNaoRealizadaException("O objeto não é uma entidade");
		}
		
		
		
		try {
			
			//valida os campos nulos.
			for(EntityField entityField: entityClass.getListaEntityField()){
				if(entityField.isNotNull()){
					
					if(PropertyUtils.getProperty(obj, entityField.getFieldName())==null){
						throw new OperacaoNaoRealizadaException("Atributo "+entityField.getFieldName()+ " não pode ser nulo.");
					}
				}
			}
			
		
			if(newObject){
				if(delegate.ext().isActive(obj)){
					throw new OperacaoNaoRealizadaException("O objeto já foi persistido.");
				}
				
				EntityId entityId = entityClass.getEntityId();
				
				if(entityId.getClazz().equals(int.class) || entityId.getClazz().equals(Integer.class)){
					
					PropertyUtils.setProperty(obj, entityId.getNomeAtributo(), 0);
					
				}else if(entityId.getClazz().equals(long.class) || entityId.getClazz().equals(Long.class)){
					PropertyUtils.setProperty(obj, entityId.getNomeAtributo(), new Long(0));
				}
			
			
			}else{
				EntityId entityId = entityClass.getEntityId();
				if(entityId!=null){
					if(PropertyUtils.getProperty(obj, entityId.getNomeAtributo())==null){
						throw new OperacaoNaoRealizadaException("A chave deve ser informado");
					}
				}
				//se o objeto não está no container devemos recuperá-lo no banco.
				if(!delegate.ext().isActive(obj)){
					ObjectSet objectSet = delegate.queryByExample(obj);
					if(objectSet.size()!= 1){
						throw new OperacaoNaoRealizadaException();
					}
					
					Object resultado = objectSet.next();
					for(EntityField entityField: entityClass.getListaEntityField()){
						PropertyUtils.setProperty(resultado, entityField.getFieldName(), 
								PropertyUtils.getProperty(obj, entityField.getFieldName()));
					}
					
					obj = resultado;
					
				}
				
			}
			
			store(obj);
			
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

}
