package org.ufrj.db4o.entityManager;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.FlushModeType;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.ufrj.db4o.exception.OperacaoNaoRealizadaException;
import org.ufrj.db4o.internal.entity.classes.EntityClass;
import org.ufrj.db4o.internal.entity.query.EntityQuery;
import org.ufrj.db4o.internal.entity.query.QueryHandler;

import com.db4o.ObjectSet;

public class QueryWrapper implements Query{

	private com.db4o.query.Query queryDb4o;
	private EntityQuery entityQuery;
	private Map<String, Object> mapaParametros;
	
	private int maxResults;
	
	
	public QueryWrapper(com.db4o.query.Query querydb4o, EntityQuery entityQuery){
		this.queryDb4o = querydb4o;
		this.entityQuery = entityQuery;
		
	}
	
	@Override
	public int executeUpdate() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List getResultList() {
		executarQuery();
		ObjectSet objectSet = queryDb4o.execute();
		
		
		if(maxResults == 0){
			maxResults= objectSet.size();
		}
		return objectSet.subList(0, maxResults);
	}
	

	private void executarQuery() {
		try {
			queryDb4o = QueryHandler.createQuery(queryDb4o, entityQuery, getMapaParametros());
		} catch (OperacaoNaoRealizadaException e) {
			throw new PersistenceException();
		}
		
	}

	@Override
	public Object getSingleResult() {
		executarQuery();
		ObjectSet objectSet = queryDb4o.execute();
		if(objectSet.size()!=1){
			throw new PersistenceException("O resultado não é único");
		}else{
			return objectSet.next();
		}
	}

	@Override
	public Query setFirstResult(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Query setFlushMode(FlushModeType arg0) {
		throw new UnsupportedOperationException("Operação não suportada pelo db4o.");
	}

	@Override
	public Query setHint(String arg0, Object arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Query setMaxResults(int arg0) {
		maxResults = arg0;
		return this;
	}

	@Override
	public Query setParameter(String arg0, Object arg1) {
		getMapaParametros().put(arg0, arg1);
		return this;
	}

	@Override
	public Query setParameter(int arg0, Object arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Query setParameter(String arg0, Date arg1, TemporalType arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Query setParameter(String arg0, Calendar arg1, TemporalType arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Query setParameter(int arg0, Date arg1, TemporalType arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Query setParameter(int arg0, Calendar arg1, TemporalType arg2) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Map<String, Object> getMapaParametros(){
		if(mapaParametros==null){
			mapaParametros = new HashMap<String, Object>();
		}
		return mapaParametros;
	}

}
