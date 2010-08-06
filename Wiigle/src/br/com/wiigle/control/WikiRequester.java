package br.com.wiigle.control;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Pattern;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

public class WikiRequester {
	
	public synchronized static HashMap<String,String> getDisambiguations(String chave) throws IOException{
		
		URL wiki = new URL("http://en.wikipedia.org/wiki/Special:Export/"+chave+"_(disambiguation)");
		URLConnection wc = wiki.openConnection();
		String conteudo = getPageContent(wc.getInputStream());
		
		//Se for uma página de redirecionamento, acessá-la e pegar seu page content
		if(conteudo.startsWith("#REDIRECT")){
			wiki = new URL("http://en.wikipedia.org/wiki/Special:Export/"+getRedirect(conteudo));
			wc = wiki.openConnection();
			conteudo = getPageContent(wc.getInputStream());
		}
		
		//Processar as desambiguações
		HashMap<String,String> colDesambiguacoes = processaDesambiguacoes(conteudo);
		
		return colDesambiguacoes;
	}
	
	private static String getPageContent(InputStream stream){
		try {
			System.out.println(stream);
			SAXBuilder sb = new SAXBuilder();
			Document d = sb.build(stream);

			Element mediawiki = d.getRootElement();
			Element page = mediawiki.getChild("page", mediawiki.getNamespace());
			Element revision = page.getChild("revision", page.getNamespace());
			Element text = revision.getChild("text", revision.getNamespace());
			
			return text.getText();

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static String getRedirect(String a){
		return a.substring(a.indexOf("[[")+2,a.indexOf("]]"));
	}
	
	private static HashMap<String,String> processaDesambiguacoes(String desambiguacoes){
		String[] linhas = desambiguacoes.split("\n");
		HashMap<String, String> retorno = new HashMap<String, String>();
		for (String linha : linhas) {
			if(!linha.startsWith("* [["))
				continue;//Não é uma desambiguação
			
			//Removendo o começo da linha
			linha = linha.replaceFirst("\\* \\[\\[", "");
			
			//Removendo o resto da linha, para ficar apenas com os termos de desambiguação
			String termo = linha.substring(0, linha.indexOf("]]"));
			if(termo.contains("|"))
				termo= termo.substring(0, termo.indexOf("|"));

			//Recuperando descrição da desambiguação
			String descricao = linha.substring(linha.indexOf(",")+1);

			retorno.put(termo.trim(), descricao.trim());
		}
		return retorno;
	}
	
	public synchronized static ArrayList<String> getLinksFromPage(String chave){
		//TODO FAZER A REQUISICAO WIKIPEDIA, E RECUPERAR O CONTEUDO DAS PAGINAS
		String conteudo ="";
		return scanLinks(conteudo);
	}
	
	private static ArrayList<String> scanLinks(String text){
		//Expressão regular que casa com os links
		String regex = "\\[\\[[^\\[]*\\]\\]";
		//Limpando o texto
		text = text.replaceAll("\\{", "");
		//Recupera os textos entre os links, para removê-los
		String[] textos = Pattern.compile(regex).split(text);
		for (int i = 0; i < textos.length; i++) {
			text = text.replaceAll(textos[i], "####");
		}
		
		return (ArrayList<String>)Arrays.asList(text.split("####"));
	}
}
