package org.ufrj.db4o;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.StringTokenizer;

public class GenericTypeInfo {    
    
    public static Class<?> extractTypeCollection(Field field){
    	//Pega o tipo de um atributo via reflection  
        Type type = field.getGenericType(); 
    	
       // System.out.println("type: " + type);    
        if (type instanceof ParameterizedType) {    
            ParameterizedType pt = (ParameterizedType) type;    
           // System.out.println("raw type: " + pt.getRawType());    
           // System.out.println("owner type: " + pt.getOwnerType());    
          //  System.out.println("actual type args:");    
 
            //Aki pega o tipo que está dentro da Collection (só interessa o último)
            Type tipo = null;
            for (Type t : pt.getActualTypeArguments()) {    
               tipo = t;
            } 
            
           StringTokenizer stk = new StringTokenizer(tipo.toString());
           String className= null;
           while(stk.countTokens()>1){stk.nextToken();}
           
           try {
			return Class.forName(stk.nextToken());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
            
        }    
     
        
    	return null;
    }
    
    public static Class<?> extractType(Field field){
    	return field.getType();
    }
     
}    
