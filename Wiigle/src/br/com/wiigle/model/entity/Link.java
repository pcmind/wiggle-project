package br.com.wiigle.model.entity;

public class Link {

	private Pagina paginaOrigem;
	
	private Pagina paginaDestino;

	private Termo alias;
	
	private boolean strongLink;
	
	public void setPaginaDestino(Pagina paginaDestino) {
		this.paginaDestino = paginaDestino;
	}

	public Pagina getPaginaDestino() {
		return paginaDestino;
	}

	public void setAlias(Termo alias) {
		this.alias = alias;
	}

	public Termo getAlias() {
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