package Teste.Entidades;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity(name="link")
public class Link {

	@ManyToOne
	@JoinColumn(name = "chave")
	private Pagina paginaOrigem;
	
	@ManyToOne
	@JoinColumn(name = "chave")
	private Pagina paginaDestino;

	@ManyToOne
	@JoinColumn(name = "termo")
	private Termo alias;
	
	@Column(name = "chave")
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
