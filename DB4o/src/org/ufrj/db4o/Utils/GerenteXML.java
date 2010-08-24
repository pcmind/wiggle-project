package org.ufrj.db4o.Utils;

import java.io.File;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.ufrj.db4o.internal.entity.Configuracao;

public class GerenteXML {

	public <T> T unmarshal( Class<T> docClass, InputStream inputStream )   throws JAXBException {
	    String packageName = docClass.getPackage().getName();
	    JAXBContext jc = JAXBContext.newInstance( packageName );
	    Unmarshaller u = jc.createUnmarshaller();
	    T doc = (T)u.unmarshal( inputStream );
	    return doc;
	    
}
	
	public Object extract(){
		
		Configuracao configuracao = new Configuracao();
		JAXBContext context;
		try {
			context = JAXBContext.newInstance("configuracao");
			Unmarshaller um = context.createUnmarshaller();
			return um.unmarshal(new File("src/contato2.xml"));
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
		
		
		
	}
	
}
