package org.ufrj.db4o.Utils;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class XMLFactory {

	public static <T> T unmarshal( Class<T> docClass, InputStream inputStream )   throws JAXBException {
	    String packageName = docClass.getPackage().getName();
	    JAXBContext jc = JAXBContext.newInstance( packageName );
	    Unmarshaller u = jc.createUnmarshaller();
	    T doc = (T)u.unmarshal( inputStream );
	    return doc;
	    
}
	
}
