package br.com.wiigle.view.vo;

public class ResultadoVO extends SugestaoVO{

	private boolean selecionado;

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}

	public boolean isSelecionado() {
		return selecionado;
	}
	
}
