package org.ufrj.db4o.internal.entity.query;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import org.ufrj.db4o.Utils.QueryOperation;
import org.ufrj.db4o.exception.OperacaoNaoRealizadaException;
import org.ufrj.db4o.internal.entity.NamedQuery;
import org.ufrj.db4o.internal.entity.classes.EntityClass;
import org.ufrj.db4o.internal.entity.classes.EntityField;
import org.ufrj.db4o.internal.entity.query.Where.TipoOperacao;

public class QueryHandler {

	public static EntityQuery create(NamedQuery namedQuery, Map<String, EntityClass> mapaEntidades) throws OperacaoNaoRealizadaException{
		if(namedQuery == null || namedQuery.getQuery() == null ||namedQuery.getQuery().trim().equals("")){
			throw new OperacaoNaoRealizadaException("NamedQuery "+ namedQuery.getName()+ " com sintaxe incorreta.");
		}
		
		
		return create(namedQuery.getQuery(), mapaEntidades);
	}
	
	public static EntityQuery create(String hql, Map<String, EntityClass> mapaEntidades) throws OperacaoNaoRealizadaException{
		
		hql = Pattern.compile("\n", Pattern.CASE_INSENSITIVE).matcher(hql).replaceAll("");
		hql = Pattern.compile("select", Pattern.CASE_INSENSITIVE).matcher(hql).replaceAll("SELECT");
		hql = Pattern.compile(" from ", Pattern.CASE_INSENSITIVE).matcher(hql).replaceAll(" FROM ");
		hql = Pattern.compile(" WHERE ", Pattern.CASE_INSENSITIVE).matcher(hql).replaceAll(" WHERE ");
		hql = Pattern.compile(" AND ", Pattern.CASE_INSENSITIVE).matcher(hql).replaceAll(" AND ");
		hql = Pattern.compile(" OR ", Pattern.CASE_INSENSITIVE).matcher(hql).replaceAll(" OR ");
		hql = Pattern.compile(" IS ", Pattern.CASE_INSENSITIVE).matcher(hql).replaceAll(" IS ");
		hql = Pattern.compile(" NOT ", Pattern.CASE_INSENSITIVE).matcher(hql).replaceAll(" NOT ");
		hql = Pattern.compile(" NULL ", Pattern.CASE_INSENSITIVE).matcher(hql).replaceAll(" NULL ");
		
		int posicao = hql.indexOf("SELECT");
		if(posicao == -1){
			throw new OperacaoNaoRealizadaException("Cláusula Select Não Encontrada.");
		}
		EntityQuery entityQuery = new EntityQuery();
		entityQuery.setSql(hql);
		
		//elimina a palavra Select
		hql = hql.substring(posicao+6);
		
		
		posicao = hql.indexOf("FROM");
		if(posicao == -1){
			throw new OperacaoNaoRealizadaException("Cláusula From Não Encontrada.");
		}
		
		//pego o select
		String proximoPasso = hql.substring(0, posicao);
		entityQuery.setSelectClause(createSelectStatment(proximoPasso));
		
		//elemino tudo q vem antes do FROM e o FROM tmb
		hql = hql.substring(posicao+4);
		
		
		posicao = hql.indexOf("WHERE");
		
		if(posicao == -1){
			entityQuery.setFromClause(createFromStatment(hql, mapaEntidades));
			validaTermo(entityQuery.getSelectString(), entityQuery.getFromClause(),mapaEntidades);
			return entityQuery;
			
		}
		
		proximoPasso = hql.substring(0, posicao);
		entityQuery.setFromClause(createFromStatment(proximoPasso, mapaEntidades));
		validaTermo(entityQuery.getSelectString(), entityQuery.getFromClause(), mapaEntidades);
		
		//elemino tudo q vem antes do WHERE e o WHERE tmb
		hql = hql.substring(posicao+5);
		entityQuery.setWhereClause(createWhereStatment(hql, entityQuery.getFromClause(), mapaEntidades));
		
		return entityQuery;
		
	}
	
	/**
	 * Responsável por verificar se o select é válido
	 * @param entityQuery
	 */
	private static void validaTermo(String termo, From from, Map<String, EntityClass> mapaEntidades) throws OperacaoNaoRealizadaException{
		StringTokenizer stk = new StringTokenizer(termo, ".");
		
		
		//valida se a classe de retorno existe no from
		Class classeRetorno = from.recuperarClasse(stk.nextToken());
		if(classeRetorno==null){
			throw new OperacaoNaoRealizadaException("Query inconsistente.");
		}else{
			EntityClass classPai = mapaEntidades.get(classeRetorno.getSimpleName());
					
			while(stk.hasMoreTokens()){
				String nextInChain = stk.nextToken();
				if(classPai == null){
					throw new OperacaoNaoRealizadaException("Query Inconsistente");
				}
				
				if(!validaFilho(nextInChain,classPai)){
					throw new OperacaoNaoRealizadaException("Query Inconsistente");
				}
				
				EntityField entityField = classPai.recuperarField(nextInChain);
				classPai = mapaEntidades.get(entityField.getClazz().getSimpleName());
								
				
			}
			
		}
		
	}
	
	private static boolean validaFilho(String nomeAtributo, EntityClass classe){
		
		for(EntityField entityField: classe.getListaEntityField()) {
			if(entityField.getFieldName().equals(nomeAtributo)){
				return true;
			}
		}
		return false;
	}

	private static Where createWhereStatment(String hql, From from, Map<String, EntityClass> mapaEntidades) throws OperacaoNaoRealizadaException{
		StringTokenizer stk = new StringTokenizer(hql, "AND");
		
		Where where = new Where();
		while(stk.hasMoreElements()){
			Operacao operacao = createWhereOperation(stk.nextToken());
			validaTermo(operacao.getLadoEsquerdo(), from, mapaEntidades);
			if(!operacao.isParametro() && !operacao.isHasValue()){
				validaTermo(operacao.getLadoDireito(), from, mapaEntidades);
			}
			where.adicionarOperacao(operacao, TipoOperacao.AND);
			
		}
		return where;
		
	}

	private static Operacao createWhereOperation(String strOperacao) throws OperacaoNaoRealizadaException{
		String[] tokens = null;;
		
		tokens = strOperacao.split(">=");
		if(tokens.length==2){
			return createOperation(tokens[0], tokens[1], QueryOperation.MAIOR_IGUAL);
			
		}
		
		tokens = strOperacao.split("=>");
		if(tokens.length==2){
			return createOperation(tokens[0], tokens[1], QueryOperation.MAIOR_IGUAL);
			
		}
		
		tokens = strOperacao.split("<=");
		if(tokens.length==2){
			return createOperation(tokens[0], tokens[1], QueryOperation.MAIOR_IGUAL);
			
		}
		
		tokens = strOperacao.split("=<");
		if(tokens.length==2){
			return createOperation(tokens[0], tokens[1], QueryOperation.MENOR_IGUAL);
			
		}
		
		tokens = strOperacao.split("<>");
		if(tokens.length==2){
			return createOperation(tokens[0], tokens[1], QueryOperation.DIFERENTE);
			
		}
		
		tokens = strOperacao.split("CONTAINS");
		if(tokens.length==2){
			return createOperation(tokens[0], tokens[1], QueryOperation.CONTAINS);
		}
		
		tokens = strOperacao.split(">");
		if(tokens.length==2){
			return createOperation(tokens[0], tokens[1], QueryOperation.MAIOR);
		}
		
		tokens = strOperacao.split("<");
		if(tokens.length==2){
			return createOperation(tokens[0], tokens[1], QueryOperation.MAIOR);
		}
		
		tokens = strOperacao.split("=");
		if(tokens.length==2){
			return createOperation(tokens[0], tokens[1], QueryOperation.IGUAL);
		}
		
		throw new OperacaoNaoRealizadaException("Query Inconsistente.");
	}

	private static Operacao createOperation(String ladoEsquerdo,
			String ladoDireito, QueryOperation queryOperation) {
		Operacao operacao = new Operacao();
		operacao.setLadoEsquerdo(ladoEsquerdo.trim());
		operacao.setOperacao(queryOperation);
		
		ladoDireito = ladoDireito.trim();
		if(ladoDireito.startsWith(":")){
			operacao.setParametro(true);
			operacao.setLadoDireito(ladoDireito.substring(1));
		}else if(ladoDireito.startsWith("'") && ladoDireito.endsWith("'")){
			operacao.setHasValue(true);
			operacao.setLadoDireito(ladoDireito.substring(1, ladoDireito.length()-1));
		}else{
			operacao.setLadoDireito(ladoDireito);
		}
		return operacao;
	}

	private static Select createSelectStatment(String select){
		StringTokenizer stk = new StringTokenizer(select, ".");
		Select selectClause = null;
		
		while(stk.hasMoreTokens()){
			if(selectClause == null){
				selectClause = new Select();
				selectClause.setAlias(stk.nextToken().trim());
			}else{
				Select subSelect = new Select();
				subSelect.setAlias(stk.nextToken().trim());
				selectClause.insereNext(subSelect);
			}
		}
		
		return selectClause;
		
	}
	
	private static From createFromStatment(String from, Map<String,EntityClass> mapaEntidades) throws OperacaoNaoRealizadaException{
		
		//gera "Class1 alias1, Class2 alias2"
		StringTokenizer stk = new StringTokenizer(from, ",");
		From fromClause = new From();
		
		while(stk.hasMoreTokens()){
			StringTokenizer stk2 = new StringTokenizer(stk.nextToken());
			
			String className = null;
			String alias = null;
			try{
			//o formato esperado é "Classe alias"
				className = stk2.nextToken();
				alias = stk2.nextToken();
			}catch(NoSuchElementException e){
				throw new OperacaoNaoRealizadaException("Claúsula From com sintaxe incorreta.");
			}
			
			
			EntityClass entityClass = mapaEntidades.get(className);
			//caso a entidade nao exista lança exceção
			if(entityClass == null){
				throw new OperacaoNaoRealizadaException("Classe não encontrada");
			}
			
			fromClause.adicionarClasse(alias, entityClass.getClazz());
		}
		return fromClause;
	}
}
