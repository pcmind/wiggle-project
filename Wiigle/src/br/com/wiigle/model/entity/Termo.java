package br.com.wiigle.model.entity;

import java.util.Date;


/**
 * Classe que representa um termo presente numa página da Wikipedia.
 * 
 * @author Pedro
 *
 */
public class Termo {

	//nome do termo.
	private String chave;
	
	private TermoIndexado termoIndexado;
	
	private Date dataExpiracao;
	
	public void setChave(String chave) {
		this.chave = chave;
	}

	public String getChave() {
		return chave;
	}

	public void setTermoIndexado(TermoIndexado termoIndexado) {
		this.termoIndexado = termoIndexado;
	}

	public TermoIndexado getTermoIndexado() {
		return termoIndexado;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Termo &&  this.getChave().equals(((Termo) obj).getChave()) ){
			
			return true;
			
		}else{
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return this.getChave().hashCode();
	}

	public void setDataExpiracao(Date dataExpiracao) {
		this.dataExpiracao = dataExpiracao;
	}

	public Date getDataExpiracao() {
		return dataExpiracao;
	}
}
