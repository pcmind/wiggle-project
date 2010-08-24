package org.ufrj.db4o.internal.entity;

public class User {
	
	private String usuario;
	
	private String senha;

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
}
