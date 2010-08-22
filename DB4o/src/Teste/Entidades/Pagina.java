package Teste.Entidades;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@Entity(name="pagina")
public class Pagina {

	//o título da página
	@Column(name = "chave")
	private String chave;
	
	@Column(name = "descricao")
	private String descricao;
	
	@Column(name = "conteudo")
	private String conteudo;
	
	@OneToMany(cascade = CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="pagina")
	private List<Link> listaLink = new ArrayList<Link>();

	public void setChave(String chave) {
		this.chave = chave;
	}

	public String getChave() {
		return chave;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setConteudo(String conteudo) {
		this.conteudo = conteudo;
	}

	public String getConteudo() {
		return conteudo;
	}

	public void setListaLink(List<Link> listaLink) {
		this.listaLink = listaLink;
	}

	public List<Link> getListaLink() {
		return listaLink;
	}
	
}
