package org.ufrj.db4o.wrapper;

public class EntityField {

	private String fieldName;
	
	private Class<?> clazz;
	
	private boolean fetchLazy;
	
	private boolean cascadeUpdate;
	
	private boolean cascadeInsert;
	
	private boolean cascadeDelete;

	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFetchLazy(boolean fetchLazy) {
		this.fetchLazy = fetchLazy;
	}

	public boolean isFetchLazy() {
		return fetchLazy;
	}

	public void setCascadeUpdate(boolean cascadeUpdate) {
		this.cascadeUpdate = cascadeUpdate;
	}

	public boolean isCascadeUpdate() {
		return cascadeUpdate;
	}

	public void setCascadeInsert(boolean cascadeInsert) {
		this.cascadeInsert = cascadeInsert;
	}

	public boolean isCascadeInsert() {
		return cascadeInsert;
	}

	public void setCascadeDelete(boolean cascadeDelete) {
		this.cascadeDelete = cascadeDelete;
	}

	public boolean isCascadeDelete() {
		return cascadeDelete;
	}
	
}