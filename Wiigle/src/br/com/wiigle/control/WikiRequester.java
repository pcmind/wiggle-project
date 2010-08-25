package br.com.wiigle.control;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
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
		Map<String,String> colDesambiguacoes=null;
		if(conteudo==null){
			//Nesse caso n�o h� pagina de desambigua��o
			colDesambiguacoes = new HashMap<String,String>();
			colDesambiguacoes.put(chave, chave);
		}else{
			//Se for uma p�gina de redirecionamento, acess�-la e pegar seu page content
			if(conteudo.toUpperCase().startsWith("#REDIRECT")){
				wiki = new URL("http://en.wikipedia.org/wiki/Special:Export/"+getRedirect(conteudo));
				wc = wiki.openConnection();
				conteudo = getPageContent(wc.getInputStream());
			}
			//Processar as desambigua��es
			colDesambiguacoes = processaDesambiguacoes(conteudo);
		}
		return colDesambiguacoes;
	}
	
	private static String getPageContent(InputStream stream){
		try {
			SAXBuilder sb = new SAXBuilder();
			Document d = sb.build(stream);

			Element mediawiki = d.getRootElement();
			Element page = mediawiki.getChild("page", mediawiki.getNamespace());
			if(page==null)
				return null;
			Element revision = page.getChild("revision", page.getNamespace());
			Element text = revision.getChild("text", revision.getNamespace());
			
			return text.getText();

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static String getRedirect(String a){
		return a.substring(a.indexOf("[[")+2,a.indexOf("]]")).replaceAll(" ", "_");
	}
	
	private static Map<String,String> processaDesambiguacoes(String desambiguacoes){
		String[] linhas = desambiguacoes.split("\n");
		HashMap<String, String> retorno = new HashMap<String, String>();
		for (String linha : linhas) {
			if(!linha.startsWith("*") || !linha.contains("[[") || !linha.contains("]]"))
				continue;//N�o � uma desambigua��o
			
			//Removendo o resto da linha, para ficar apenas com os termos de desambigua��o
			String termo = linha.substring(linha.indexOf("[[")+2, linha.indexOf("]]"));
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
		URL wiki = new URL("http://en.wikipedia.org/wiki/Special:Export/"+pag.getChave().replaceAll(" ", "_"));
		URLConnection wc = wiki.openConnection();
		System.out.println("Recuperando conte�do de " + pag.getChave().replaceAll(" ", "_"));
		pag.setConteudo(getPageContent(wc.getInputStream()));
		if(pag.getConteudo()==null){
			System.err.println("N�o foi poss�vel recuperar o conte�do de "+pag.getChave().replaceAll(" ", "_"));
			return new ArrayList<Link>();
		}
		return scanLinks(pag);
	}
	
	private static List<Link> scanLinks(Pagina pagina) throws Exception{
		
		String text = pagina.getConteudo(); 
		String[] links = text.split("\\[\\[");
		List<Link> listaLink = new ArrayList<Link>();
		//Pula o primeiro termo do array porque ele n�o cont�m nenhum link
		for (int i = 1; i < links.length; i++) {
			//Criar o link e jogar no resultado
			String alias = links[i];
			if(alias.toUpperCase().startsWith("IMAGE:")
					|| alias.toUpperCase().startsWith("FILE:"))
				continue;
			String linkName = null;
			if(alias.indexOf("|")>-1 && alias.indexOf("|")<alias.indexOf("]]")){
				linkName = alias.substring(alias.indexOf("|")+1, alias.indexOf("]]"));
				alias = alias.substring(0, alias.indexOf("|"));
			}
			else{
				alias = alias.substring(0, alias.indexOf("]]"));
				linkName=alias;
			}
			
			Link link = new Link();
			link.setPaginaOrigem(pagina);
			link.setPaginaDestino(new Pagina()); //tenho f� que conseguiremos catar no banco.
			link.setLinkName(linkName);
			link.setAlias(alias);
			//TODO nao verifica se � strong link ainda
			link.setStrongLink(true);
			listaLink.add(link);
			
		}
		return listaLink;
	}
}
