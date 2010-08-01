package br.com.wiigle.view.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.apache.commons.beanutils.PropertyUtils;

public class EntityConverter implements Converter {

	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if(value == null || value.trim().equals("")) return null;
		
		return component.getAttributes().get(value.trim());
	}

	public String getAsString(FacesContext context, UIComponent component, Object obj) {
		
		String asString = getAsString(obj);
		if (asString != null) component.getAttributes().put(asString.trim(), obj);
				
		return asString;
	}

	public String getAsString(Object obj){
		if(obj == null) return null;
		
		try{
			String asString = "";

			Class clazz = obj.getClass();
			
			while (clazz != null){
				
				Field[] fields = clazz.getDeclaredFields();
	
				for(Field field: fields){
					
					Annotation[] annotations = field.getAnnotations();
	
					for(Annotation annotation: annotations){
						if(annotation.annotationType().equals(Id.class)){
							if(!asString.trim().equals(""))  asString += "/";
							
							Object idObject = PropertyUtils.getProperty(obj, field.getName());
							
							String internString = getAsString(idObject);
							
							if(internString.trim().equals(""))
								asString += idObject.toString();
							else
								asString += internString;
							break;
						}else if(annotation.annotationType().equals(Entity.class)){
							String internAsString = getAsString(PropertyUtils.getProperty(obj, field.getName()));
							if(internAsString != null && !internAsString.trim().equals("")){
								if(!asString.trim().equals(""))  asString += "/";
								asString += internAsString;
							}
						}
					}
				}
				clazz = clazz.getSuperclass();
			}

			return asString;
			
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return null;	
	}
	

}

