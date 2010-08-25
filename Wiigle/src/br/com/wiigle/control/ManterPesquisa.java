package br.com.wiigle.control;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.com.wiigle.model.entity.Link;
import br.com.wiigle.model.entity.Pagina;
import br.com.wiigle.model.entity.Termo;
import br.com.wiigle.model.entity.TermoIndexado;
import br.com.wiigle.view.utils.Consulta;

/**
 * Classe responsável por implementar as funcionalidades da interface Pesquisa.
 * 
 * @author Pedro
 *
 */
public class ManterPesquisa implements IManterPesquisa{

	
	private final int NUMERO_PALAVRAS_RELEVANTES = 5;
	
	
	public List<String> desambiguacaoRapida(String consulta){
		return null;
	}
	
	public List<String> desambiguacao(Consulta consulta) {
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
	private List<String> desambiguacao(File file){
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
	
	private List<String> desambiguacao(String consulta){
		try{
			//Aplicar os algoritmos de processamento de texto (StopWords, Porter, etc.)
			Set<TermoIndexado> listaTermo = TextProcessor.processText(consulta);
			
			//Fazer as contagens de palavras, ou TF-IDF, e pegar as palavras que mais aparecem
			List<Termo> relevantWords = TextProcessor.getRelevantWords(listaTermo,NUMERO_PALAVRAS_RELEVANTES);
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
			
			//Comparar em qual domínio apareceram mais os outros termos
			//TODO atualmente pega apenas a pagina que mais aparece.
			Pagina paginaRelevante = (Pagina) TextProcessor.sortByValue(contagem, null).get(0);
			
			//Enviar resposta do domínio ao usuário
			//TODO atualmente apenas imprime no console
			System.out.println("O texto fornecido fala sobre " + paginaRelevante.getChave() + ", " + 
					paginaRelevante.getDescricao());
		
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	

}
