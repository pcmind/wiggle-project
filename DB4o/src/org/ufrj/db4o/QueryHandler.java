package org.ufrj.db4o;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.ufrj.db4o.exception.OperacaoNaoRealizadaException;
import org.ufrj.db4o.wrapper.EntityClass;
import org.ufrj.db4o.wrapper.EntityQuery;
import org.ufrj.db4o.wrapper.From;
import org.ufrj.db4o.wrapper.NamedQuery;
import org.ufrj.db4o.wrapper.Select;

public class QueryHandler {

	public static EntityQuery create(NamedQuery namedQuery, Map<String, EntityClass> mapaEntidades) throws OperacaoNaoRealizadaException{
		if(namedQuery == null || namedQuery.getQuery() == null ||namedQuery.getQuery().trim().equals("")){
			throw new OperacaoNaoRealizadaException("NamedQuery "+ namedQuery.getName()+ " com sintaxe incorreta.");
		}
		
		
		return create(namedQuery.getQuery(), mapaEntidades);
	}
	
	public static EntityQuery create(String hql, Map<String, EntityClass> mapaEntidades) throws OperacaoNaoRealizadaException{
		
		hql = hql.replaceAll("[Ss][Ee][Ll][Ee][Cc][Tt]", "SELECT");
		hql = hql.replaceAll("[Ff][Rr][Oo][Mm]", "FROM");
		hql = hql.replaceAll("[Ww][Hh][Ee][Rr][Ee]", "WHERE");
		hql = hql.replaceAll("[Aa][Nn][Dd]", "AND");
		hql = hql.replaceAll("[Oo][Rr]", "OR");
		hql = hql.replaceAll("[Cc][Oo][Nn][Tt][Aa][Ii][Nn][Ss]", "CONTAINS");
				
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
		String select = hql.substring(0, posicao);
		entityQuery.setSelectClause(createSelectStatment(select));
		
		//elemino tudo q vem antes do FROM e o FROM tmb
		hql = hql.substring(posicao+4);
		
		
		posicao = hql.indexOf("WHERE");
		if(posicao == -1){
			throw new OperacaoNaoRealizadaException("Cláusula Where Não Encontrada.");
		}
		//pego o from
		String from = hql.substring(0, posicao);
		entityQuery.setFromClause(createFromStatment(select, mapaEntidades));
		
		validaSelect(entityQuery, mapaEntidades);
		
		//elemino tudo q vem antes do WHERE e o WHERE tmb
		hql = hql.substring(posicao+5);
		createWhereStatment(hql, mapaEntidades);
		
		return entityQuery;
		
	}
	
	/**
	 * Responsável por verificar se o select é válido
	 * @param entityQuery
	 */
	private static void validaSelect(EntityQuery entityQuery, Map<String, EntityClass> mapaEntidades) {
		//TODO
		
	}

	private static void createWhereStatment(String hql,
			Map<String, EntityClass> mapaEntidades) {
		// TODO Auto-generated method stub
		
	}

	private static Select createSelectStatment(String select){
		StringTokenizer stk = new StringTokenizer(select, ".");
		Select selectClause = null;
		
		while(stk.hasMoreTokens()){
			if(selectClause == null){
				selectClause = new Select();
				selectClause.setAlias(stk.nextToken());
			}else{
				Select subSelect = new Select();
				subSelect.setAlias(stk.nextToken());
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
