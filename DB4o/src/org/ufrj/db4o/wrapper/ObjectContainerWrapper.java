package org.ufrj.db4o.wrapper;

import java.util.Comparator;
import java.util.Map;

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
	
	public ObjectContainerWrapper(ObjectContainer objectContainer, Map<String, EntityClass> mapaEntidades){
		this.delegate = objectContainer;
		this.mapaEntidades = mapaEntidades;
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
	
	

}
