package br.com.wiigle.control;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

public class TextProcessor {
	
	private static String stopWordRegex = null;
	
	public synchronized static String processText(String texto) throws Exception{
		//TODO Ainda não foi testado - adaptei do meu trabalho do Xexeo
		texto = processaCaracteres(texto);
		texto = removeStopWords(texto);
		texto = aplicaPorter(texto);
		texto = transformaEmMaiusculas(texto);
		
		return null;
	}
	
	private static String transformaEmMaiusculas(String texto) {
		return texto.toUpperCase();
	}

	private static String aplicaPorter(String texto) {
		PorterStemmer ps = new PorterStemmer();
		
		texto = texto.toLowerCase();
		
		// Para cada palavra do texto, aplicar o Porter:
		StringTokenizer palavras = new StringTokenizer(texto);
		String resultado = texto;
		while(palavras.hasMoreTokens()){
			String token = palavras.nextToken();
			resultado = resultado.replace(token, ps.stem(token));
		}
		return resultado;
	}

	private static String removeStopWords(String texto) throws Exception {
		if(stopWordRegex == null)
			stopWordRegex = recuperarStopWords();
		
		texto = Pattern.compile(stopWordRegex, Pattern.CASE_INSENSITIVE).matcher(texto).replaceAll("");
		while(texto.contains("  "))
			texto = texto.replaceAll("  ", " ");
		return texto;
		
	}
	
	private static String recuperarStopWords() throws Exception{
		
		BufferedReader in = new BufferedReader(new FileReader("model/stopWordList.txt"));
		String str;
		String stopWords = "\\b(";
		while((str = in.readLine()) != null){
			stopWords += str.toUpperCase() + "|";
		}
		in.close();
		stopWords = stopWords.substring(0, stopWords.length()-1);
		stopWords += ")\\b";
		return stopWords;
		
	}

	private static String processaCaracteres(String texto) {
		return Normalizer.normalize(texto).replaceAll("[\\W&&[^\\s]]", "");
	}
	
	public synchronized static ArrayList<String> getRelevantWords(String text){
		return null;
	}
}
