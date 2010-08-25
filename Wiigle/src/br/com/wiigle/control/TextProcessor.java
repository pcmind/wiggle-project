package br.com.wiigle.control;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import br.com.wiigle.model.entity.Termo;
import br.com.wiigle.model.entity.TermoIndexado;

public class TextProcessor {
	
	private static String stopWordRegex = null;
	
	public synchronized static Set<TermoIndexado> processText(String texto) throws Exception{
		texto = transformaEmMinusculas(texto);
		texto = processaCaracteres(texto);
		texto = removeStopWords(texto);
		Set<TermoIndexado> listaTermo = aplicaPorter(texto);
		
		return listaTermo;
	}
	
	private static String transformaEmMinusculas(String texto) {
		return texto.toLowerCase();
	}

	private static Set<TermoIndexado> aplicaPorter(String texto) {
		PorterStemmer ps = new PorterStemmer();
		
		Set<TermoIndexado> listaTermo =  new HashSet<TermoIndexado>();
		HashMap<String, TermoIndexado> mapaTermo= new HashMap<String, TermoIndexado>();
		// Para cada palavra do texto, aplicar o Porter:
		StringTokenizer palavras = new StringTokenizer(texto);
		TermoIndexado termoIndexado;
		while(palavras.hasMoreTokens()){
			String palavra = palavras.nextToken();
			String palavraProcessada = ps.stem(palavra);
			if(palavraProcessada.equals("Invalid term"))
				palavraProcessada = palavra;
			termoIndexado = mapaTermo.get(palavraProcessada);
			if(termoIndexado == null)
				termoIndexado = new TermoIndexado();
			Termo termo = new Termo();
			termo.setChave(palavra);
			termoIndexado.setChave(palavraProcessada); 
			termo.setTermoIndexado(termoIndexado);
			termoIndexado.getListaTermo().add(termo);
			termoIndexado.setQtdRelevante(termoIndexado.getQtdRelevante()+1);
			mapaTermo.put(palavraProcessada, termoIndexado);
		}
		listaTermo.addAll(mapaTermo.values());
		return listaTermo;
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
		
		BufferedReader in = new BufferedReader(new FileReader(Thread.currentThread().getContextClassLoader().getResource("br/com/wiigle/model/stopWordList.txt").getFile().replaceAll("%20", " ")));
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
	public synchronized static List<Termo> getRelevantWords(Set<TermoIndexado> setTermo, Integer max){
		
		List<TermoIndexado> listaTermoIndexado = new ArrayList<TermoIndexado>(setTermo);
		
		//ordem decrescente
		Collections.sort(listaTermoIndexado, new Comparator<TermoIndexado>() {
			public int compare(TermoIndexado o1, TermoIndexado o2) {
				return ((Integer)o2.getQtdRelevante()).compareTo((Integer)o1.getQtdRelevante());
			}
		});
		List<Termo> listaTermo = new ArrayList<Termo>();
		for(int i = 0; i< max; i++){
			listaTermo.addAll(listaTermoIndexado.get(i).getListaTermo());
		}
		
		return listaTermo;
	}
	
	@SuppressWarnings("unchecked")
	public synchronized static List sortByValue(Map<?, Integer> map, Integer max) {
		
		
		List<Map.Entry> list = new ArrayList<Map.Entry>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry>() {
			public int compare(Map.Entry o1, Map.Entry o2) {
				return ((Integer)o2.getValue()).compareTo((Integer)o1.getValue());
			}
		});
		List resultado = new ArrayList();
		for (Iterator iter = list.iterator(); iter.hasNext();) {
			Map.Entry element = (Map.Entry) iter.next();
			resultado.add(element.getKey());
			if(max!=null && max<=resultado.size())
				break;
		}
		return resultado;
	}

}
