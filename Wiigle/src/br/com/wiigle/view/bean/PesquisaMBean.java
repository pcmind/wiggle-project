package br.com.wiigle.view.bean;


import java.util.ArrayList;
import java.util.Date;

import br.com.wiigle.control.TermoHandler;
import br.com.wiigle.control.TextProcessor;
import br.com.wiigle.control.WikiRequester;
import br.com.wiigle.model.Termo;

public class PesquisaMBean {
	
	private String textoConsulta;
	
	@PostConstruct
	public void inicializar(){
		System.out.println("Passou aqui");
	}

	public void desambiguar(){
		//Recupera o texto digitado
		String chave = textoConsulta;
		
		try{
			//TODO Aplicar os algoritmos de processamento de texto (StopWords, Porter, etc.)
			chave = TextProcessor.processText(chave);
			//TODO Fazer as contagens de palavras, ou TF-IDF, e pegar as palavras que mais aparecem
			ArrayList<String> relevantWords = TextProcessor.getRelevantWords(chave);
			//TODO Para as palavras que mais aparecem, recuperar possíveis desambiguações
			for (String palavra : relevantWords) {
				//Recupera termo com chave = palavra
				Termo termo = TermoHandler.findByKey(palavra);
				//TODO Para cada conjunto de links da desambiguação, fazer contagem dos outros termos
				
			}
			
			//TODO Comparar em qual domínio apareceram mais os outros termos
			
			
			//TODO Enviar resposta do domínio ao usuário
			
			
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
