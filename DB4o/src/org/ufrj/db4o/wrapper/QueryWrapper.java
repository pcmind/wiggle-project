package org.ufrj.db4o.wrapper;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.FlushModeType;
import javax.persistence.Query;
import javax.persistence.TemporalType;

public class QueryWrapper implements Query{

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
		// TODO Auto-generated method stub
		return null;
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
