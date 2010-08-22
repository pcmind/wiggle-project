package org.ufrj.db4o.wrapper;

public class EntityId {

	private Class<?> clazz;
	
	//nome do atributo da classe que usa esse identificador.
	private String nomeAtributo;
	
	private boolean autoIncrement;
	
	public EntityId(){
		this.autoIncrement = false;
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
	
}
