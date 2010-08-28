package br.com.wiigle.control;

import java.util.List;

import br.com.wiigle.view.utils.Consulta;
import br.com.wiigle.view.vo.ResultadoVO;
import br.com.wiigle.view.vo.SugestaoVO;

/**
 * Interface que define as operações de pesquisa que existem. 
 * 
 * @author Pedro
 *
 */
public interface IManterPesquisa {

	public List<ResultadoVO> desambiguacao(Consulta consulta);
	
	/**
	 * Desambiguacao do suggestionBox
	 * @param consulta
	 * @return
	 */
	public List<SugestaoVO> desambiguacaoRapida(String consulta);
	
}
