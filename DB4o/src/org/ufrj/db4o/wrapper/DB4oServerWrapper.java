package org.ufrj.db4o.wrapper;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBException;

import org.ufrj.db4o.Configuracao;
import org.ufrj.db4o.EntityHandler;
import org.ufrj.db4o.QueryHandler;
import org.ufrj.db4o.ScanEntity;
import org.ufrj.db4o.User;
import org.ufrj.db4o.XMLFactory;
import org.ufrj.db4o.exception.InvalidLoginException;
import org.ufrj.db4o.exception.OperacaoNaoRealizadaException;

import com.db4o.Db4o;
import com.db4o.Db4oEmbedded;
import com.db4o.EmbeddedObjectContainer;
import com.db4o.ObjectContainer;
import com.db4o.ObjectServer;
import com.db4o.ObjectSet;
import com.db4o.config.ConfigScope;
import com.db4o.config.Configuration;
import com.db4o.config.ObjectClass;
import com.db4o.config.ObjectField;
import com.db4o.cs.config.ClientConfiguration;
import com.db4o.cs.config.ServerConfiguration;
import com.db4o.cs.internal.config.ClientConfigurationImpl;
import com.db4o.cs.internal.config.ServerConfigurationImpl;
import com.db4o.ext.DatabaseFileLockedException;
import com.db4o.ext.DatabaseReadOnlyException;
import com.db4o.ext.Db4oIOException;
import com.db4o.ext.ExtObjectServer;
import com.db4o.ext.IncompatibleFileFormatException;
import com.db4o.ext.InvalidPasswordException;
import com.db4o.ext.OldFormatException;
import com.db4o.internal.Config4Impl;

public class DB4oServerWrapper{
	
public final static int ARBITRARY_PORT = -1;
private final static String ARQUIVO_CONFIGURACAO = "arquivos/Config.xml";

//configurações default de cliente e servidor
public static boolean ALLOW_VERSION_UPDATE = true;
public static boolean AUTOMATIC_SHUTDOWN = true;
public static int BTREE_SIZE = 100;
public static boolean CALLBACKS = false;
public static boolean CALL_CONSTRUCTORS = false;
public static int MESSAGE_LEVEL = 2;
public static boolean OPTMIZE_NATIVE_QUERY = true;
public static boolean TEST_CONSTRUCTORS = false;
public static int UPDATE_DEPTH = 1;
public static int TIMEOUT_SOCKET = 600000;
public static int BLOCK_SIZE = 8;
//----------------------------------------------
	
	
	private static Map<String, EntityClass> mapaEntidades;
	
	private static Map<String, EntityQuery> mapaNamedQuery;

	static{
		if(mapaEntidades==null){
			mapaEntidades = new HashMap<String, EntityClass>();
			mapaNamedQuery = new HashMap<String, EntityQuery>();
			List<NamedQuery> listaNamedQuery = new ArrayList<NamedQuery>();
			Set<Class<?>> classes = ScanEntity.findAll(Thread.currentThread().getContextClassLoader());
			
			try {
			
				for(Class<?> clazz : classes){
					
						mapaEntidades.put(clazz.getSimpleName(), EntityHandler.create(clazz));
						listaNamedQuery.addAll(EntityHandler.namedQueries(clazz));
						
					
				}
				
				
				for(NamedQuery namedQuery : listaNamedQuery){
					mapaNamedQuery.put(namedQuery.getName(), QueryHandler.create(namedQuery, mapaEntidades));
					
				}
			} catch (OperacaoNaoRealizadaException e) {
				throw new Db4oIOException(e);
			}
			
			
		}
		
		
	}

	/**
	 * creates a new {@link ServerConfiguration}
	 */
	public static ServerConfiguration newServerConfiguration() {
		return new ServerConfigurationImpl(newLegacyConfig());
	}

	/**
     * opens an {@link ObjectServer ObjectServer}
	 * on the specified database file and port.
     * <br><br>
	 * @param config a custom {@link ServerConfiguration} instance to be obtained via {@link #newServerConfiguration()}
     * @param databaseFileName an absolute or relative path to the database file
     * @param port the port to be used or 0 if the server should not open a port, specify a value < 0 if an arbitrary free port should be chosen - see {@link ExtObjectServer#port()}.
	 * @return an {@link ObjectServer ObjectServer} listening
	 * on the specified port.
     * @see Configuration#readOnly
     * @see Configuration#encrypt
     * @see Configuration#password
     * @throws Db4oIOException I/O operation failed or was unexpectedly interrupted.
     * @throws DatabaseFileLockedException the required database file is locked by 
     * another process.
     * @throws IncompatibleFileFormatException runtime 
     * {@link com.db4o.config.Configuration configuration} is not compatible
     * with the configuration of the database file. 
     * @throws OldFormatException open operation failed because the database file
     * is in old format and {@link com.db4o.config.Configuration#allowVersionUpdates(boolean)} 
     * is set to false.
     * @throws DatabaseReadOnlyException database was configured as read-only. 
	 */
	public static ObjectServer openServer(ServerConfiguration config,
			String databaseFileName, int port) {
		
		
		if(config==null){
			config = newServerConfiguration();
			configuracaoPadraoServidor(config);
			configuraEntidades(config);
		}
		
		ObjectServer objectServer = config.networking().clientServerFactory().openServer(config, databaseFileName, port);
		objectServer = new ObjectServerWrapper(objectServer);
		
		EmbeddedObjectContainer container = Db4oEmbedded.openFile(databaseFileName.substring(0, databaseFileName.length()-4)+".config.yap");
		
		ObjectSet<User> objectSet = container.query(User.class);
		for(User user : objectSet){
			objectServer.grantAccess(user.getUsuario(), user.getSenha());
		}
		container.close();
		
		return objectServer;
		
	}

	/**
	 * opens a db4o server with a fresh server configuration.
	 * 
	 * @see #openServer(ServerConfiguration, String, int)
	 * @see #newServerConfiguration()
	 */
	public static ObjectServer openServer(String databaseFileName, int port) {
	
		//faz sentido um xml para configuração de servidor? acho q não.
		//Configuracao configXML = recuperarConfiguracaoXML();
		
		return openServer(null, databaseFileName, port);
	}

	private static void configuraEntidades(ServerConfiguration config) {
		for(EntityClass entityClass: mapaEntidades.values()){
			ObjectClass objectClass = config.common().objectClass(entityClass.getClazz());
			
			for(EntityField entityField : entityClass.getListaEntityField()){
				ObjectField objectField = objectClass.objectField(entityField.getFieldName());
				objectField.cascadeOnActivate(!entityField.isFetchLazy());
				objectField.cascadeOnDelete(entityField.isCascadeDelete());
				objectField.cascadeOnUpdate(entityField.isCascadeUpdate());
			}
			
		}
	}


	
	
	private static void configuracaoPadraoServidor(ServerConfiguration config) {
		
		//config.common().activationDepth(0);
		//config.common().addAlias(null);
		config.common().allowVersionUpdates(ALLOW_VERSION_UPDATE); //c - s
		config.common().automaticShutDown(AUTOMATIC_SHUTDOWN); // c - s
		config.common().bTreeNodeSize(BTREE_SIZE); //default 100, c - s
		config.common().callbacks(CALLBACKS); //c - s
		config.common().callConstructors(CALL_CONSTRUCTORS);
		//config.common().detectSchemaChanges(true); //c - s
		//config.common().exceptionsOnNotStorable(true); // c - s
		//config.common().maxStackDepth(20);
		config.common().messageLevel(MESSAGE_LEVEL);
		//config.common().nameProvider(null);
		config.common().optimizeNativeQueries(OPTMIZE_NATIVE_QUERY);
		config.common().outStream(System.out);
		//config.common().registerTypeHandler(null, null);
		//config.common().removeAlias(null);
		//config.common().stringEncoding(null);
		config.common().testConstructors(TEST_CONSTRUCTORS); //c - s
		config.common().updateDepth(UPDATE_DEPTH);
		config.timeoutServerSocket(TIMEOUT_SOCKET); //deixa o default q tá, tirar o 0
		//TODO config.networking().messageRecipient(null);
		//config.networking().singleThreadedClient(false);
		//TODO Socket4Factory socketFactory = config.networking().socketFactory();
		
		config.file().blockSize(BLOCK_SIZE);
		//config.file().databaseGrowthSize(0); // sei la
		config.file().generateUUIDs(ConfigScope.INDIVIDUALLY);
		config.file().generateVersionNumbers(ConfigScope.INDIVIDUALLY);
					
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

	/**
	 * opens a db4o client instance with the specified configuration.
	 * @param config the configuration to be used
	 * @param host the host name of the server that is to be connected to
	 * @param port the server port to connect to
	 * @param user the username for authentication
	 * @param password the password for authentication
	 * @see #openServer(ServerConfiguration, String, int)
	 * @see ObjectServer#grantAccess(String, String)
	 * @throws IllegalArgumentException if the configuration passed in has already been used.
	 */
	public static ObjectContainer openClient(ClientConfiguration config,
			String host, int port, String user, String password) throws InvalidLoginException{
		
		//configuração nula possibilita as configurações automáticas;
		if(config==null){
			config = newClientConfiguration();
			configuracaoPadraoClient(config);
			configuraEntidades(config);
		}
		
		
		ObjectContainer container = null;
		try{
		 container = config.networking().clientServerFactory().openClient(config, host, port, user, password);
		 
		}catch(InvalidPasswordException e){
			throw new InvalidLoginException("Usuário ou senha inválido", e);
		}
		
		return new ObjectContainerWrapper(container, mapaEntidades, mapaNamedQuery);
	}
	
	private static void configuraEntidades(ClientConfiguration config) {
		for(EntityClass entityClass: mapaEntidades.values()){
			ObjectClass objectClass = config.common().objectClass(entityClass.getClazz());
			
			for(EntityField entityField : entityClass.getListaEntityField()){
				ObjectField objectField = objectClass.objectField(entityField.getFieldName());
				objectField.cascadeOnActivate(!entityField.isFetchLazy());
				objectField.cascadeOnDelete(entityField.isCascadeDelete());
				objectField.cascadeOnUpdate(entityField.isCascadeUpdate());
			}
			
		}
		
	}

	/**
	 * opens a db4o client instance with a fresh client configuration.
	 * 
	 * @see #openClient(ClientConfiguration, String, int, String, String)
	 * @see #newClientConfiguration()
	 */
	public static ObjectContainer openClient(String host, int port, String user, String password) throws InvalidLoginException{
		return openClient(null, host, port, user, password);
	}
	
	private static void configuracaoPadraoClient(ClientConfiguration config) {
			
		//config.common().activationDepth(0);
		//config.common().addAlias(null);
		config.common().allowVersionUpdates(ALLOW_VERSION_UPDATE); //c - s
		config.common().automaticShutDown(AUTOMATIC_SHUTDOWN); // c - s
		config.common().bTreeNodeSize(BTREE_SIZE); //default 100, c - s
		config.common().callbacks(CALLBACKS); //c - s
		config.common().callConstructors(CALL_CONSTRUCTORS);
		//config.common().detectSchemaChanges(true); //c - s
		//config.common().exceptionsOnNotStorable(true); // c - s
		//config.common().maxStackDepth(20);
		config.common().messageLevel(MESSAGE_LEVEL);
		//config.common().nameProvider(null);
		config.common().optimizeNativeQueries(OPTMIZE_NATIVE_QUERY);
		config.common().outStream(System.out);
		//config.common().registerTypeHandler(null, null);
		//config.common().removeAlias(null);
		//config.common().stringEncoding(null);
		config.common().testConstructors(TEST_CONSTRUCTORS); //c - s
		config.common().updateDepth(UPDATE_DEPTH);
		config.timeoutClientSocket(TIMEOUT_SOCKET); //deixa o default q tá, tirar o 0
		//TODO config.networking().messageRecipient(null);
		//config.networking().singleThreadedClient(false);
		//TODO Socket4Factory socketFactory = config.networking().socketFactory();
		
				
				
	}
	
	/**
	 * creates a new {@link ClientConfiguration} 
	 */
	public static ClientConfiguration newClientConfiguration() {
		final Config4Impl legacy = newLegacyConfig();
		return new ClientConfigurationImpl((Config4Impl) legacy);
	}
	
    @SuppressWarnings("deprecation")
    private static Config4Impl newLegacyConfig() {
		return (Config4Impl) Db4o.newConfiguration();
	}

}


  
  
