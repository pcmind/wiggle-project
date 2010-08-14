package br.com.wiigle.control;

import java.util.List;

import br.com.wiigle.view.utils.Consulta;

/**
 * Interface que define as operações de pesquisa que existem. 
 * 
 * @author Pedro
 *
 */
public interface IManterPesquisa {

	public List<String> desambiguacao(Consulta consulta);
	
}
