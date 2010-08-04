package br.com.wiigle.model;

import java.util.List;

public class Pagina {
	private String chave;
	private List<String> desambiguacoes;
	
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
}
