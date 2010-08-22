package Teste.Entidades;

import java.util.ArrayList;
import java.util.List;

public class Pagina {

	//o título da página
	private String chave;
	
	private String descricao;
	
	private String conteudo;
	
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
