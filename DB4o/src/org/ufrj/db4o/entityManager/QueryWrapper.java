package org.ufrj.db4o.entityManager;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.FlushModeType;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.ufrj.db4o.internal.entity.query.EntityQuery;

public class QueryWrapper implements Query{

	private com.db4o.query.Query queryDb4o;
	private EntityQuery entityQuery;
	
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getSingleResult() {
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Query setParameter(String arg0, Object arg1) {
		// TODO Auto-generated method stub
		return null;
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

}
