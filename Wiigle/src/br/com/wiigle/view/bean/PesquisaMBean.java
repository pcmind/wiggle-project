package br.com.wiigle.view.bean;

import javax.annotation.PostConstruct;

public class PesquisaMBean {

	private String textoConsulta;
	
	@PostConstruct
	public void inicializar(){
		System.out.println("Passou aqui");
	}

	public void desambiguar(){
		System.out.println("Desambiguação");
		//return "teste";
	}
	
	public void setTextoConsulta(String textoConsulta) {
		this.textoConsulta = textoConsulta;
	}

	public String getTextoConsulta() {
		return textoConsulta;
	}
	
}
