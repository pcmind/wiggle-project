package br.com.wiigle.model;

import java.util.List;

public class Pagina {
	private String chave;
	private List<String> links;
	
	public String getChave() {
		return chave;
	}
	
	public void setChave(String chave) {
		this.chave = chave;
	}
	
	public List<String> getLinks() {
		return links;
	}
	
	public void setLinks(List<String> links) {
		this.links = links;
	}
}
