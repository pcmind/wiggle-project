package br.com.wiigle.model.entity;

public class Link {

	private Pagina paginaOrigem;
	
	private Pagina paginaDestino;

	private String alias;
	
	private String linkName;

	private boolean strongLink;
	
	public void setPaginaDestino(Pagina paginaDestino) {
		this.paginaDestino = paginaDestino;
	}
	
	public String getLinkName() {
		return linkName;
	}

	public void setLinkName(String linkName) {
		this.linkName = linkName;
	}

	public Pagina getPaginaDestino() {
		return paginaDestino;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getAlias() {
		return alias;
	}

	public void setStrongLink(boolean strongLink) {
		this.strongLink = strongLink;
	}

	public boolean isStrongLink() {
		return strongLink;
	}

	public void setPaginaOrigem(Pagina paginaOrigem) {
		this.paginaOrigem = paginaOrigem;
	}

	public Pagina getPaginaOrigem() {
		return paginaOrigem;
	}
	
	
	
}
