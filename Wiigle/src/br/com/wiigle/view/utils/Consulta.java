package br.com.wiigle.view.utils;

public class Consulta {
	
	private String valor;
	
	private boolean path;

	public Consulta(){
		path = false;
	}
	public void setValor(String valor) {
		this.valor = valor;
	}

	public String getValor() {
		return valor;
	}

	public void setPath(boolean path) {
		this.path = path;
	}

	public boolean isPath() {
		return path;
	}
	
}
