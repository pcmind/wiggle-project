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
	
	/**
	 * Desambiguacao do suggestionBox
	 * @param consulta
	 * @return
	 */
	public List<String> desambiguacaoRapida(String consulta);
	
}
