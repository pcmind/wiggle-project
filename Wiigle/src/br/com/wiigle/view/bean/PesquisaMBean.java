package br.com.wiigle.view.bean;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import br.com.wiigle.control.TermoHandler;
import br.com.wiigle.control.TextProcessor;
import br.com.wiigle.model.Pagina;
import br.com.wiigle.model.Termo;

public class PesquisaMBean {
	
	private String textoConsulta;
	private final int NUMERO_PALAVRAS_RELEVANTES = 5;
	@PostConstruct
	public void inicializar(){
		System.out.println("Passou aqui");
	}

	public void desambiguar(){
		//Recupera o texto digitado
		String chave = textoConsulta;
		
		try{
			//Aplicar os algoritmos de processamento de texto (StopWords, Porter, etc.)
			chave = TextProcessor.processText(chave);
			//Fazer as contagens de palavras, ou TF-IDF, e pegar as palavras que mais aparecem
			ArrayList<String> relevantWords = TextProcessor.getRelevantWords(chave,NUMERO_PALAVRAS_RELEVANTES);
			//Map com as paginas e contagens:
			HashMap<Pagina, Integer> contagem = new HashMap<Pagina, Integer>();
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

	}
	
	public void setTextoConsulta(String textoConsulta) {
		this.textoConsulta = textoConsulta;
	}

	public String getTextoConsulta() {
		return textoConsulta;
	}
	
}
