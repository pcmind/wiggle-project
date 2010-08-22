package Teste.Entidades;

import javax.persistence.Entity;

import org.hibernate.validator.NotNull;

@Entity
public class EntidadeTeste {

	@NotNull
	private String nome;
	
	private EntidadeFilha filha;

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getNome() {
		return nome;
	}

	public void setFilha(EntidadeFilha filha) {
		this.filha = filha;
	}

	public EntidadeFilha getFilha() {
		return filha;
	}
	
}
