package br.com.wiigle.model;

import java.util.Date;
import java.util.List;

public class Termo {
	
	private String chave;
	private List<Pagina> desambiguacoes;
	private Date dataExpiracao;
	
	public String getChave() {
		return chave;
	}
	
	public void setChave(String chave) {
		this.chave = chave;
	}
	
	public List<Pagina> getDesambiguacoes() {
		return desambiguacoes;
	}
	
	public void setDesambiguacoes(List<Pagina> desambiguacoes) {
		this.desambiguacoes = desambiguacoes;
	}
	
	public Date getDataExpiracao() {
		return dataExpiracao;
	}
	
	public void setDataExpiracao(Date dataExpiracao) {
		this.dataExpiracao = dataExpiracao;
	}
	
	
}
