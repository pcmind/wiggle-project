package br.com.wiigle.model;

import java.util.Date;
import java.util.List;

public class Termo {
	
	private String chave;
	private List<String> desambiguacoes;
	private Date dataExpiracao;
	
	public String getChave() {
		return chave;
	}
	
	public void setChave(String chave) {
		this.chave = chave;
	}
	
	public List<String> getDesambiguacoes() {
		return desambiguacoes;
	}
	
	public void setDesambiguacoes(List<String> desambiguacoes) {
		this.desambiguacoes = desambiguacoes;
	}
	
	public Date getDataExpiracao() {
		return dataExpiracao;
	}
	
	public void setDataExpiracao(Date dataExpiracao) {
		this.dataExpiracao = dataExpiracao;
	}
	
	
}
