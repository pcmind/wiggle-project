package br.com.wiigle.view.vo;

public class SugestaoVO {

	private String termo;
	
	private String descricao;
	
	private String busca;

	public void setTermo(String termo) {
		this.termo = termo;
	}

	public String getTermo() {
		return termo;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}
	
	public String getBusca(){
		return busca;
	}

	public void setBusca(String busca) {
		this.busca = busca;
	}
	
}
