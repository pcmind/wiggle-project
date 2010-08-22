package org.ufrj.db4o.wrapper;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Classe que representa todas as configurações de uma entidade.
 * @author Pedro
 *
 */
public class EntityClass {

	private Class<?> clazz;
	
	private String alias;
	
	private EntityId entityId; 
	
	private Map<String, EntityField> fieldsMap;

	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

	public Class<?> getClazz() {
		return clazz;
	}
	
	private Map<String, EntityField> getFieldsMap(){
		if(this.fieldsMap == null){
			fieldsMap = new HashMap<String, EntityField>();
		}
		return fieldsMap;
	}
	
	public void adicionarField(EntityField entityField){
		getFieldsMap().put(entityField.getFieldName(), entityField);
	}
	
	public Collection<EntityField> getListaEntityField(){
		return getFieldsMap().values();
	}
	
	public EntityField recuperarField(String nomeField){
		return getFieldsMap().get(nomeField);
	}
	
	public Set<String> getListaNomeField(){
		return this.fieldsMap.keySet();
	}

	public void setEntityId(EntityId entityId) {
		this.entityId = entityId;
	}

	public EntityId getEntityId() {
		return entityId;
	}
	
	public String getFieldIdName(){
		return entityId.getNomeAtributo();
	}
	
	public Class<?> getFieldIdClass(){
		return entityId.getClazz();
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getAlias() {
		return alias;
	}
	
}
