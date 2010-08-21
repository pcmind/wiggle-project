package br.com.wiigle.control;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import br.com.wiigle.model.entity.Link;
import br.com.wiigle.model.entity.Pagina;
import br.com.wiigle.model.entity.Termo;

public class WikiRequester {
	
	public synchronized static Map<String,String> getDisambiguations(String chave) throws IOException{
		
		URL wiki = new URL("http://en.wikipedia.org/wiki/Special:Export/"+chave+"_(disambiguation)");
		URLConnection wc = wiki.openConnection();
		String conteudo = getPageContent(wc.getInputStream());
		
		//Se for uma p�gina de redirecionamento, acess�-la e pegar seu page content
		if(conteudo.startsWith("#REDIRECT")){
			wiki = new URL("http://en.wikipedia.org/wiki/Special:Export/"+getRedirect(conteudo));
			wc = wiki.openConnection();
			conteudo = getPageContent(wc.getInputStream());
		}
		
		//Processar as desambigua��es
		Map<String,String> colDesambiguacoes = processaDesambiguacoes(conteudo);
		
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
	
	private static Map<String,String> processaDesambiguacoes(String desambiguacoes){
		String[] linhas = desambiguacoes.split("\n");
		HashMap<String, String> retorno = new HashMap<String, String>();
		for (String linha : linhas) {
			if(!linha.startsWith("* [["))
				continue;//N�o � uma desambigua��o
			
			//Removendo o come�o da linha
			linha = linha.replaceFirst("\\* \\[\\[", "");
			
			//Removendo o resto da linha, para ficar apenas com os termos de desambigua��o
			String termo = linha.substring(0, linha.indexOf("]]"));
			if(termo.contains("|"))
				termo= termo.substring(0, termo.indexOf("|"));

			//Recuperando descri��o da desambigua��o
			String descricao = linha.substring(linha.indexOf(",")+1);

			retorno.put(termo.trim(), descricao.trim());
		}
		return retorno;
	}
	
	public synchronized static List<Link> getLinksFromPage(Pagina pag) throws Exception{
		
		//Fazer requisi��o wikipedia pela chave, e recuperar seu conte�do
		URL wiki = new URL("http://en.wikipedia.org/wiki/Special:Export/"+pag.getChave());
		URLConnection wc = wiki.openConnection();
		pag.setConteudo(getPageContent(wc.getInputStream()));
		
		return scanLinks(pag);
	}
	
	private static List<Link> scanLinks(Pagina pagina) throws Exception{
		//Express�o regular que casa com os links
		String regex = "\\[\\[[^\\[]*\\]\\]";
		String text = pagina.getConteudo(); 
		
		//Limpando o texto
		text = text.replaceAll("\\{", "");
		//Recupera os textos entre os links, para remov�-los
		String[] textos = Pattern.compile(regex).split(text);
		for (int i = 0; i < textos.length; i++) {
			text = text.replaceAll(textos[i], "####");
		}
		String[] links = text.split("####");
		List<Link> listaLink = new ArrayList<Link>();
		for (int i = 0; i < links.length; i++) {
			//Criar o link e jogar no resultado
			//t� errado ainda, deu sono xD
			Link link = new Link();
			link.setPaginaOrigem(pagina);
			link.setPaginaDestino(new Pagina()); //tenho f� que conseguiremos catar no banco.
			
			//onde t� o alias ?
			link.setAlias(new Termo());
			//TODO nao verifica se � strong link ainda
			link.setStrongLink(true);
			listaLink.add(link);
			
		}
		return listaLink;
	}
}
