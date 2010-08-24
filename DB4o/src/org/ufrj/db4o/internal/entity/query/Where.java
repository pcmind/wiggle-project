package org.ufrj.db4o.internal.entity.query;

import java.util.ArrayList;
import java.util.List;


public class Where {

	public enum TipoOperacao {
		AND,
		OR;
	}
	
	private List<Operacao> listaOperacoes;

	public void setListaOperacoes(List<Operacao> listaOperacoes) {
		this.listaOperacoes = listaOperacoes;
	}

	protected List<Operacao> getListaOperacoes() {
		if(listaOperacoes==null){
			listaOperacoes = new ArrayList<Operacao>();
		}
		return listaOperacoes;
	}
	
	public void adicionarOperacao(Operacao operacao, TipoOperacao tipoOperacao){
		if(tipoOperacao.equals(TipoOperacao.AND)){
			//adiciona na propria lista
			getListaOperacoes().add(operacao);
		}else if(tipoOperacao.equals(TipoOperacao.AND)){
			//adiciona na lista do ultimo and que entrou
			getListaOperacoes().get(getListaOperacoes().size()-1).getListaOperacoes().add(operacao);
		}
	}
	
	
	
}
