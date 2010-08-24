package org.ufrj.db4o.entityManager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.xml.bind.JAXBException;

import org.ufrj.db4o.Utils.XMLFactory;
import org.ufrj.db4o.exception.InvalidLoginException;
import org.ufrj.db4o.exception.OperacaoNaoRealizadaException;
import org.ufrj.db4o.internal.entity.Configuracao;
import org.ufrj.db4o.internal.entity.query.EntityQuery;
import org.ufrj.db4o.wrapper.DB4oServerWrapper;
import org.ufrj.db4o.wrapper.ObjectContainerWrapper;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.ext.DatabaseClosedException;
import com.db4o.ext.DatabaseReadOnlyException;
import com.db4o.ext.Db4oIOException;

public class EntityManagerWrapper implements EntityManager{

	private final static String ARQUIVO_CONFIGURACAO = "arquivos/Config.xml";
	
	ObjectContainer objectContainer;
	Configuracao configXML;
	//mapa de querys que são criadas em runtime e já foram utilizadas pelo menos uma vez
	Map<String, EntityQuery> mapaQueryRuntime;
	
	public EntityManagerWrapper() {
		
		
		configXML = recuperarConfiguracaoXML();
		if(configXML==null){
			throw new PersistenceException("Não foi possível encontrar config.xml");
		}
		inicializaObjectContainer();
		
	}
	
	private void inicializaObjectContainer(){
		if(objectContainer == null){
			try {
				objectContainer = DB4oServerWrapper.openClient(configXML.getHostName(), configXML.getPort(), configXML.getUsuario(), configXML.getSenha());
			} catch (InvalidLoginException e) {
				throw new PersistenceException("Usuário ou senah inválido",e);
				
			}
		}
	}
	
	@Override
	public void clear() {
		if(objectContainer!=null){
			objectContainer.close();
		}
		objectContainer=null;
		inicializaObjectContainer();
	}

	@Override
	public void close() {
		if(objectContainer!=null){
			
			try{
				objectContainer.close();
			}catch(Db4oIOException e){
				throw new IllegalStateException();
			}
		}
	}

	@Override
	public boolean contains(Object arg0) {
		return getObjectContainer().ext().isStored(arg0);
	}

	@Override
	public Query createNamedQuery(String arg0) {
		EntityQuery entityQuery = null;
		try {
			entityQuery = ((ObjectContainerWrapper)getObjectContainer()).criarNamedQuery(arg0);
		} catch (OperacaoNaoRealizadaException e) {
			throw new PersistenceException();
		}
		return new QueryWrapper(getObjectContainer().query(), entityQuery);
	}

	@Override
	public Query createNativeQuery(String arg0) {
		throw new UnsupportedOperationException("Operação não é suportada por db4o", null);
	}

	@Override
	public Query createNativeQuery(String arg0, Class arg1) {
		throw new UnsupportedOperationException("Operação não é suportada por db4o", null);
	}

	@Override
	public Query createNativeQuery(String arg0, String arg1) {
		throw new UnsupportedOperationException("Operação não é suportada por db4o", null);
	}

	@Override
	public Query createQuery(String arg0) {
		EntityQuery entityQuery = getMapaQueryRuntime().get(arg0);
		
		//caso a query nao tenha sido executada previamente ela tem q ser gerada.
		if(entityQuery==null){
			try {
				entityQuery =  ((ObjectContainerWrapper)getObjectContainer()).criarQuery(arg0);
			} catch (OperacaoNaoRealizadaException e) {
				throw new PersistenceException();
			}
			//insiro a query gerada no entityManager para otimizar uma nova execução.
			getMapaQueryRuntime().put(arg0, entityQuery);
		}
		return new QueryWrapper(getObjectContainer().query(), entityQuery);
	}

	@Override
	public <T> T find(Class<T> arg0, Object arg1) {
		try {
			return ((ObjectContainerWrapper)getObjectContainer()).find(arg0, arg1);
		} catch (OperacaoNaoRealizadaException e) {
			throw new PersistenceException(e);
		}
	}

	@Override
	public void flush() {
		
		try{
			getObjectContainer().commit();
		}catch(Db4oIOException e){
			throw new IllegalStateException(e);
		}catch(DatabaseClosedException e){
			throw new IllegalStateException(e);
		}catch(DatabaseReadOnlyException e){
			throw new IllegalStateException(e);
		}
		
	}

	@Override
	public Object getDelegate() {
		return getObjectContainer();
	}

	@Override
	public FlushModeType getFlushMode() {
		throw new UnsupportedOperationException("Operação não é suportada por db4o", null);
		
	}

	@Override
	public <T> T getReference(Class<T> arg0, Object arg1) {
		return find(arg0, arg1);
	}

	@Override
	public EntityTransaction getTransaction() {
		throw new UnsupportedOperationException("Operação não é suportada por db4o", null);
		
	}

	@Override
	public boolean isOpen() {
		if(objectContainer==null){
			return true;
		}else{
			return !getObjectContainer().ext().isClosed();
		}
	}

	@Override
	public void joinTransaction() {
		throw new UnsupportedOperationException("Operação não é suportada por db4o", null);
		
	}

	@Override
	public void lock(Object arg0, LockModeType arg1) {
		throw new UnsupportedOperationException("Operação não é suportada por db4o", null);
		
	}

	@Override
	public <T> T merge(T arg0) {
		try {
			((ObjectContainerWrapper)getObjectContainer()).store(arg0, false);
		} catch (DatabaseClosedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OperacaoNaoRealizadaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.flush();
		return arg0;
		
	}

	@Override
	public void persist(Object arg0) {
		try {
			((ObjectContainerWrapper)getObjectContainer()).store(arg0, true);
		} catch (DatabaseClosedException e) {
			throw new PersistenceException();
		} catch (OperacaoNaoRealizadaException e) {
			throw new PersistenceException();
		}
		
	}

	@Override
	public void refresh(Object arg0) {
		getObjectContainer().ext().refresh(arg0, 1);
		
	}

	@Override
	public void remove(Object arg0) {
		
		try{
		if(getObjectContainer().ext().isActive(arg0)){
			getObjectContainer().delete(arg0);
			return;
		}
		
		ObjectSet objectSet = getObjectContainer().queryByExample(arg0);
		if(objectSet.size()==0){
			throw new PersistenceException("Objeto passado não foi persistido anteriormente.");
		}
		if(objectSet.size()>1){
			throw new PersistenceException("Objeto está duplicado.");
		}
			
		getObjectContainer().delete(objectSet.next());
		}catch(Db4oIOException e){
			throw new IllegalArgumentException(e);
		}catch(DatabaseClosedException e){
			throw new IllegalStateException(e);
		}catch(DatabaseReadOnlyException e){
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void setFlushMode(FlushModeType arg0) {
		throw new UnsupportedOperationException("Operação não é suportada por db4o", null);
	}
	
	private ObjectContainer getObjectContainer(){
		inicializaObjectContainer();
		return objectContainer;
	}
	
	/**
	 * Método que recupera os dados do arquivo xml de configuração do servidor.
	 * @return
	 */
	private static Configuracao recuperarConfiguracaoXML() {
		try {
			return XMLFactory.unmarshal(Configuracao.class, new FileInputStream( ARQUIVO_CONFIGURACAO ));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private Map<String, EntityQuery> getMapaQueryRuntime(){
		if(mapaQueryRuntime==null){
			mapaQueryRuntime = new HashMap<String, EntityQuery>();
		}
		
		return mapaQueryRuntime;
	}

}
