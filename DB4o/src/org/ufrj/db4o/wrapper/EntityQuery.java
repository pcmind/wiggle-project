package org.ufrj.db4o.wrapper;

public class EntityQuery {

	private Select selectClause;
	
	private From fromClause;
	
	private Where whereClause;
	
	private String sql;

	public void setSelectClause(Select selectClause) {
		this.selectClause = selectClause;
	}

	private Select getSelectClause() {
		return selectClause;
	}

	public void setFromClause(From fromClause) {
		this.fromClause = fromClause;
	}

	private From getFromClause() {
		return fromClause;
	}

	public void setWhereClause(Where whereClause) {
		this.whereClause = whereClause;
	}

	private Where getWhereClause() {
		return whereClause;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getSql() {
		return sql;
	}
}
