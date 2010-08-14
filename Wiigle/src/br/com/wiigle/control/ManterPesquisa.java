package br.com.wiigle.control;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.wiigle.model.Pagina;
import br.com.wiigle.model.Termo;
import br.com.wiigle.view.utils.Consulta;

/**
 * Classe responsável por implementar as funcionalidades da interface Pesquisa.
 * 
 * @author Pedro
 *
 */
public class ManterPesquisa implements IManterPesquisa{

	
	private final int NUMERO_PALAVRAS_RELEVANTES = 5;
	
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
			consulta = TextProcessor.processText(consulta);
			
			//Fazer as contagens de palavras, ou TF-IDF, e pegar as palavras que mais aparecem
			List<String> relevantWords = TextProcessor.getRelevantWords(consulta,NUMERO_PALAVRAS_RELEVANTES);
			//Map com as paginas e contagens:
			Map<Pagina, Integer> contagem = new HashMap<Pagina, Integer>();
			//Para as palavras que mais aparecem, recuperar possíveis desambiguações
			for (String palavra : relevantWords) {
				//Recupera termo com chave = palavra
				Termo termo = TermoHandler.findByKey(palavra);
				//Para cada conjunto de links da desambiguação, fazer contagem dos outros termos
				for (Pagina pagina : termo.getDesambiguacoes()) {
					for (String link : pagina.getLinks()) {
						if(relevantWords.contains(link)){
							//Adicionar na contagem
							Integer i = contagem.get(pagina);
							if(i!=null)
								i++;
							else
								contagem.put(pagina, 1);
						} // end if contagem links
					} // end if links da pagina
				} // end if paginas do termo
				
			} // end if palavras relevantes
			
			//Comparar em qual domínio apareceram mais os outros termos
			//TODO atualmente pega apenas a pagina que mais aparece.
			Pagina paginaRelevante = (Pagina) TextProcessor.sortByValue(contagem, null).get(0);
			
			//Enviar resposta do domínio ao usuário
			//TODO atualmente apenas imprime no console
			System.out.println("O texto fornecido fala sobre " + paginaRelevante.getChave() + 
					paginaRelevante.getDescricao());
		
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	

}
