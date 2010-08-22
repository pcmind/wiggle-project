package org.ufrj.db4o;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class GenericTypeInfo {    
    java.util.List<? extends Foo> fooList = new ArrayList<Bar>();    
     
    public static void main(String[] args) throws Exception {    
        Field field = GenericTypeInfo.class.getDeclaredField("fooList");    
     
//Pega o tipo de um atributo via reflection  
        Type type = field.getGenericType();  
   
        System.out.println("type: " + type);    
        if (type instanceof ParameterizedType) {    
            ParameterizedType pt = (ParameterizedType) type;    
            System.out.println("raw type: " + pt.getRawType());    
            System.out.println("owner type: " + pt.getOwnerType());    
            System.out.println("actual type args:");    
 
//Aki pega o tipo que está dentro do arrayList  
            for (Type t : pt.getActualTypeArguments()) {    
                System.out.println("    " + t);    
            }    
        }    
     
        System.out.println();    
     
        Object obj = field.get(new GenericTypeInfo());    
        System.out.println("obj: " + obj);    
       System.out.println("obj class: " + obj.getClass());    
    }    
     
    static class Foo {}    
     
    static class Bar extends Foo {}    
}    
