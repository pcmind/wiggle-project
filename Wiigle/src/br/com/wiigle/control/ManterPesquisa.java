package br.com.wiigle.control;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import br.com.wiigle.model.entity.Link;
import br.com.wiigle.model.entity.Pagina;
import br.com.wiigle.model.entity.Termo;
import br.com.wiigle.model.entity.TermoIndexado;
import br.com.wiigle.view.utils.Consulta;
import br.com.wiigle.view.vo.ResultadoVO;
import br.com.wiigle.view.vo.SugestaoVO;

/**
 * Classe responsável por implementar as funcionalidades da interface Pesquisa.
 * 
 * @author Pedro
 *
 */
public class ManterPesquisa implements IManterPesquisa{

	
	private final int NUMERO_PALAVRAS_RELEVANTES = 5;
	
	
	public List<SugestaoVO> desambiguacaoRapida(String consulta){
		
		List<SugestaoVO> listaSugestoes = new ArrayList<SugestaoVO>();
		Map<String, String> mapaSugestoes = null;
		try {
			mapaSugestoes = WikiRequester.getDisambiguations(consulta);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(mapaSugestoes!=null){
			SugestaoVO sugestao = null;
			Pattern pattern = Pattern.compile("[()]", Pattern.CASE_INSENSITIVE);
			for(String termo : mapaSugestoes.keySet() ){
				sugestao = new SugestaoVO();
				sugestao.setTermo(termo);
				sugestao.setBusca(pattern.matcher(termo).replaceAll("\""));
				sugestao.setDescricao(mapaSugestoes.get(termo));
				listaSugestoes.add(sugestao);
			}
		}
		
		return listaSugestoes;
	}
	
	public List<ResultadoVO> desambiguacao(Consulta consulta) {
		if(consulta.isPath()){
			File file = new File(consulta.getValor());
			return desambiguacao(file);
		}else{
			return desambiguacao(consulta.getValor());
		}
	}
	
	/**
	 * Abre o arquivo texto e extrai o conteúdo.
	 * @param file
	 * @return
	 */
	private List<ResultadoVO> desambiguacao(File file){
		StringBuffer sb = new StringBuffer(); 
		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			String conteudoLinha;
			
			while((conteudoLinha = in.readLine())!=null ){ 
				sb.append(conteudoLinha);
			}
				
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		}
		
		return desambiguacao(sb.toString());
	}
	
	private List<ResultadoVO> desambiguacao(String consulta){
		
		List<ResultadoVO> listaResultado = new ArrayList<ResultadoVO>();
		
		try{
			//Aplicar os algoritmos de processamento de texto (StopWords, Porter, etc.)
			Set<TermoIndexado> listaTermo = TextProcessor.processText(consulta);
			
			int numeroPalavrasRelevantes = calculaQtdadePalavrasRelevantes(listaTermo.size());
			//Fazer as contagens de palavras, ou TF-IDF, e pegar as palavras que mais aparecem
			List<Termo> relevantWords = TextProcessor.getRelevantWords(listaTermo,numeroPalavrasRelevantes);
			//Map com as paginas e contagens:
			Map<Pagina, Integer> contagem = new HashMap<Pagina, Integer>();
			
			//Lista a ser usada para comparar com os Links de cada pagina de desambiguação
			Set<String> stemmedRelevantWords = new HashSet<String>();
			for (Termo termo : relevantWords) {
				stemmedRelevantWords.add(termo.getTermoIndexado().getChave());
			}
			
			//Para as palavras que mais aparecem, recuperar possíveis desambiguações
			for (Termo palavra : relevantWords) {
				//Recupera termo com chave = palavra
				Termo termo = TermoHandler.findByKey(palavra.getChave());
				//Para cada conjunto de links da desambiguação, fazer contagem dos outros termos
				for (Pagina pagina : termo.getPaginasDeDesambiguacao()) {
					for (Link link : pagina.getListaLink()) {
						Set<TermoIndexado> linkProcessado = TextProcessor.processText(link.getLinkName());
						for (TermoIndexado parteLink : linkProcessado) {
							if(stemmedRelevantWords.contains(parteLink.getChave())){
								Integer i = contagem.get(pagina);
								if(i==null)
									contagem.put(pagina, 1);
								else
									contagem.put(pagina, ++i);
							}
						}
						
						//O link pode conter tanto Alias quanto LinkName
						//Se forem diferentes, comparo com ambos
						if(!link.getLinkName().equals(link.getAlias())){
							Set<TermoIndexado> aliasProcessado = TextProcessor.processText(link.getAlias());
							for (TermoIndexado parteAlias : aliasProcessado) {
								if(stemmedRelevantWords.contains(parteAlias.getChave())){
									Integer i = contagem.get(pagina);
									if(i==null)
										contagem.put(pagina, 1);
									else
										contagem.put(pagina, ++i);
								}
							}
						}
					} 
				} 
				
			}
			
			int qtdadeResultadosRelevantes = calculaQtdadeResultadosRelevantes(listaTermo.size(),numeroPalavrasRelevantes, contagem.size());
			List<Pagina> listaPaginas =  TextProcessor.sortByValue(contagem, qtdadeResultadosRelevantes);
			
			
			ResultadoVO resultado = null;
			Pattern pattern = Pattern.compile("[()]", Pattern.CASE_INSENSITIVE);
			for(Pagina pagina :listaPaginas){
				resultado = new ResultadoVO();
				resultado.setSelecionado(false);
				resultado.setTermo(pagina.getChave());
				resultado.setDescricao(pagina.getDescricao());
				resultado.setBusca(pattern.matcher(pagina.getChave()).replaceAll("\""));
				listaResultado.add(resultado);
			}
			
		
		}catch(Exception e){
			e.printStackTrace();
		}
		return listaResultado;
	}

	private int calculaQtdadeResultadosRelevantes(int qtadeTermosExistentesTexto,
			int numeroPalavrasRelevantesUtilizadas, int qtdadeDominiosEncontrados) {
		
		int numeroResultadosRelevante = (int) (numeroPalavrasRelevantesUtilizadas*0.4);
		if(numeroResultadosRelevante<2){
			return 2;
		}else if(numeroResultadosRelevante> 10){
			return 10;
		}else{
			return numeroResultadosRelevante;
		}
	}

	private int calculaQtdadePalavrasRelevantes(int qtdadeTermos) {
		int qtdadeTermosRelevantes = (int) (qtdadeTermos*0.05);
		if(qtdadeTermosRelevantes < NUMERO_PALAVRAS_RELEVANTES){
			return NUMERO_PALAVRAS_RELEVANTES;
		}else{
			return qtdadeTermosRelevantes;
		}
	}
	

}
