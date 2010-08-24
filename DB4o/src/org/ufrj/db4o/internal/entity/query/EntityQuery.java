package org.ufrj.db4o.internal.entity.query;

public class EntityQuery {

	private Select selectClause;
	
	private From fromClause;
	
	private Where whereClause;
	
	private String sql;

	public void setSelectClause(Select selectClause) {
		this.selectClause = selectClause;
	}

	protected Select getSelectClause() {
		return selectClause;
	}

	public void setFromClause(From fromClause) {
		this.fromClause = fromClause;
	}

	protected From getFromClause() {
		return fromClause;
	}

	public void setWhereClause(Where whereClause) {
		this.whereClause = whereClause;
	}

	protected Where getWhereClause() {
		return whereClause;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getSql() {
		return sql;
	}
	
	public String getSelectString(){
		StringBuffer sb = new StringBuffer();
		Select next = this.selectClause;
		while(next!=null){
			sb.append(next.getAlias()+".");
			next = next.getNext();
		}
		return sb.toString().substring(0,sb.length()-1);
	}
}
