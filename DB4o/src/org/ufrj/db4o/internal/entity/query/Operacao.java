package org.ufrj.db4o.internal.entity.query;

import java.util.ArrayList;
import java.util.List;

import org.ufrj.db4o.Utils.QueryOperation;

public class Operacao {

	//lista de operações OR dentro de uma operação and
	private List<Operacao> listaOperacoes;

	private String ladoEsquerdo;
	
	private QueryOperation operacao;
	
	private String ladoDireito;
	
	//define se o lado direito da operacao é um parametro
	private boolean parametro;
	
	//define se o lado direito é um literal
	private boolean hasValue;
	
	public Operacao(){
		parametro = false;
		hasValue = false;
	}
	
	public void setListaOperacoes(List<Operacao> listaOperacoes) {
		this.listaOperacoes = listaOperacoes;
	}

	public List<Operacao> getListaOperacoes() {
		if(listaOperacoes==null){
			listaOperacoes = new ArrayList<Operacao>();
		}
		return listaOperacoes;
	}

	public void setLadoEsquerdo(String ladoEsquerdo) {
		this.ladoEsquerdo = ladoEsquerdo;
	}

	public String getLadoEsquerdo() {
		return ladoEsquerdo;
	}

	public void setOperacao(QueryOperation operacao) {
		this.operacao = operacao;
	}

	public QueryOperation getOperacao() {
		return operacao;
	}

	public void setLadoDireito(String ladoDireito) {
		this.ladoDireito = ladoDireito;
	}

	public String getLadoDireito() {
		return ladoDireito;
	}

	public void setParametro(boolean parametro) {
		this.parametro = parametro;
	}

	public boolean isParametro() {
		return parametro;
	}

	public void setHasValue(boolean hasValue) {
		this.hasValue = hasValue;
	}

	public boolean isHasValue() {
		return hasValue;
	}
	
}
