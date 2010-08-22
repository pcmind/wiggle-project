package org.ufrj.db4o;

import java.io.File;  
import java.io.IOException;  
import java.net.URISyntaxException;  
import java.net.URL;  
import java.net.URLClassLoader;  
import java.util.Collections;  
import java.util.Enumeration;  
import java.util.HashSet;
import java.util.Set;  
import java.util.jar.JarFile;  
import java.util.zip.ZipEntry;  


public class ScanClass {
	
	  
	public static Set<Class<?>> findAll(ClassLoader classLoader, Set<String> locations,  
	            Set<String> packages) {  
	        if (!(classLoader instanceof URLClassLoader)) {  
	            return null;  
	        }  
	  
	        Set<Class<?>> classes = new HashSet<Class<?>>();
	        
	        URLClassLoader urlLoader = (URLClassLoader) classLoader;  
	        URL[] urls = urlLoader.getURLs();  
	  
	        for (URL url : urls) {  
	            String path = url.getFile();  
	            File location = null;  
	            try {  
	                location = new File(url.toURI());  
	            } catch (URISyntaxException e) {  
	                e.printStackTrace();  
	                return null;  
	            }  
	  
	            // Só processa a URL se ela coincidir com uma das strings
	            if (matchesAny(path, locations)) {  
	                if (location.isDirectory()) {  
	                   getClassesInDirectory(null, location, packages, classes);  
	                } else {  
	                	//jar não é necessário agora
	                    //getClassesInJar(location, packages);  
	                }  
	            }  
	        }
	        return classes;
	    }  
	  
	    public static Set<Class<?>> getClassesInDirectory(String parent, File location,  
	            Set<String> packagePatterns, Set<Class<?>> classes) {  
	        File[] files = location.listFiles();  
	        StringBuilder builder = null;  
	  
	        for (File file : files) {  
	            builder = new StringBuilder(100);  
	            builder.append(parent).append("/").append(file.getName());  
	            String packageOrClass = (parent == null ? file.getName() : builder  
	                    .toString());  
	  
	            if (file.isDirectory()) {  
	                getClassesInDirectory(packageOrClass, file, packagePatterns, classes);  
	            } else if (file.getName().endsWith(".class")) {  
	                if (matchesAny(packageOrClass, packagePatterns)) {  
	                    System.out.println(packageOrClass); 
	                    try {
							classes.add(Class.forName(packageOrClass.replace(".class", "").replaceAll("/", ".")));
						} catch (ClassNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	                }  
	            }  
	        }
	        
	        return classes;
	    }  
	  
	    public static Set<Class<?>> getClassesInJar(File location,  
	            Set<String> packagePatterns) {  
	        
	    	Set<Class<?>> classes = new HashSet<Class<?>>();
	    	try {  
	            JarFile jar = new JarFile(location);  
	            Enumeration entries = jar.entries();  
	  
	            while (entries.hasMoreElements()) {  
	                ZipEntry entry = (ZipEntry) entries.nextElement();  
	                String name = entry.getName();  
	                if (!entry.isDirectory() && name.endsWith(".class")) {  
	                    if (matchesAny(name, packagePatterns)) {  
	                        System.out.println(name); 
	                        
								classes.add(Class.forName(name));
							}
	                    }  
	                }  
	             
	        } catch (IOException ioe) {  
	            ioe.printStackTrace();  
	        } catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	        return classes;
	    }  
	  
	    public static boolean matchesAny(String text, Set<String> filters) {  
	        if (filters == null || filters.size() == 0) {  
	            return true;  
	        }  
	        for (String filter : filters) {  
	            if (text.indexOf(filter) != -1) {  
	                return true;  
	            }  
	        }  
	        return false;  
	    }  
	  
	  
}  

