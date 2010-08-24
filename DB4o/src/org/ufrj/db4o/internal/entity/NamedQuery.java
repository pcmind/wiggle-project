package org.ufrj.db4o.internal.entity;

public class NamedQuery {

	private String name;
	
	private String query;

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getQuery() {
		return query;
	}
	
}
