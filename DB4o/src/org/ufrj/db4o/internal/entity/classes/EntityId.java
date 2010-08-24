package org.ufrj.db4o.internal.entity.classes;

public class EntityId {

	private Class<?> clazz;
	
	//nome do atributo da classe que usa esse identificador.
	private String nomeAtributo;
	
	private boolean autoIncrement;
	
	private boolean notNull;
	
	public EntityId(){
		this.autoIncrement = false;
		this.notNull = true;
	}
	
	public void setClazz(Class<?> clazz) {
		if(clazz!=null){
			this.clazz = clazz;
			
		}
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public void setNomeAtributo(String nomeAtributo){
		this.nomeAtributo = nomeAtributo;
	}
	
	public String getNomeAtributo() {
		return nomeAtributo;
	}

		public void setAutoIncrement(boolean autoIncrement) {
			this.autoIncrement = autoIncrement;
		}

		public boolean isAutoIncrement() {
			return autoIncrement;
		}

		public void setNotNull(boolean notNull) {
			this.notNull = notNull;
		}

		public boolean isNotNull() {
			return notNull;
		}
	
}
