package org.ufrj.db4o;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Configuracao {
	
	private String host;
	
	private String usuario;
	
	private String senha;

	public void setHost(String host) {
		this.host = host;
	}

	public String getHost() {
		return host;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getSenha() {
		return senha;
	}
	
	public String getHostName(){
		int posicao = host.indexOf(":");
		if(posicao!= -1){
			return host.substring(0, posicao);
		}
		
		return host;
	}
	
	public int getPort(){
		int posicao = host.indexOf(":");
		if(posicao!= -1){
			return Integer.parseInt(host.substring(posicao+1));
		}
		
		return 0;
	}


	
}
