package br.com.wiigle.control;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

public class TextProcessor {
	
	private static String stopWordRegex = null;
	
	public synchronized static String processText(String texto) throws Exception{
		//TODO Ainda não foi testado - adaptei do meu trabalho do Xexeo
		texto = transformaEmMinusculas(texto);
		texto = processaCaracteres(texto);
		texto = removeStopWords(texto);
		texto = aplicaPorter(texto);
		
		
		return texto;
	}
	
	private static String transformaEmMinusculas(String texto) {
		return texto.toLowerCase();
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
	
	@SuppressWarnings("unchecked")
	public synchronized static List<String> getRelevantWords(String text, Integer max){
		Map<String, Integer> contagem = new HashMap<String, Integer>();
		
		//Para cada palavra do texto, contar o número de palavras
		StringTokenizer palavras = new StringTokenizer(text);
		while(palavras.hasMoreTokens()){
			String token = palavras.nextToken();
			Integer value = contagem.get(token);
			if(value!=null)
				value++;
			else
				contagem.put(token, Integer.valueOf(1));
		}
		
		//Ordenar o map por ordem decrescente do número de palavras
		List<String> resultado = sortByValue(contagem, max);
		return resultado;
	}
	
	@SuppressWarnings("unchecked")
	public synchronized static List sortByValue(Map<?, Integer> map, Integer max) {
		List<Map.Entry> list = new ArrayList<Map.Entry>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry>() {
			public int compare(Map.Entry o1, Map.Entry o2) {
				return ((Integer)o2.getValue()).compareTo((Integer)o1.getValue());
			}
		});
		ArrayList resultado = new ArrayList();
		for (Iterator iter = resultado.iterator(); iter.hasNext();) {
			Map.Entry element = (Map.Entry) iter.next();
			resultado.add(element.getKey());
			if(max!=null && max<=resultado.size())
				break;
		}
		return resultado;
	}

}
