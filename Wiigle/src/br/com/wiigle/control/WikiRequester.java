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
			//Nesse caso não há pagina de desambiguação
			colDesambiguacoes = new HashMap<String,String>();
			colDesambiguacoes.put(chave, chave);
		}else{
			//Se for uma página de redirecionamento, acessá-la e pegar seu page content
			if(conteudo.toUpperCase().startsWith("#REDIRECT")){
				wiki = new URL("http://en.wikipedia.org/wiki/Special:Export/"+getRedirect(conteudo));
				wc = wiki.openConnection();
				conteudo = getPageContent(wc.getInputStream());
			}
			//Processar as desambiguações
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
				continue;//Não é uma desambiguação
			
			//Removendo o resto da linha, para ficar apenas com os termos de desambiguação
			String termo = linha.substring(linha.indexOf("[[")+2, linha.indexOf("]]"));
			if(termo.contains("|"))
				termo= termo.substring(0, termo.indexOf("|"));

			//Recuperando descrição da desambiguação
			String descricao = linha.substring(linha.indexOf(",")+1);

			retorno.put(termo.trim(), descricao.trim());
		}
		return retorno;
	}
	
	public synchronized static List<Link> getLinksFromPage(Pagina pag) throws Exception{
		
		//Fazer requisição wikipedia pela chave, e recuperar seu conteúdo
		URL wiki = new URL("http://en.wikipedia.org/wiki/Special:Export/"+pag.getChave().replaceAll(" ", "_"));
		URLConnection wc = wiki.openConnection();
		System.out.println("Recuperando conteúdo de " + pag.getChave().replaceAll(" ", "_"));
		pag.setConteudo(getPageContent(wc.getInputStream()));
		if(pag.getConteudo()==null){
			System.err.println("Não foi possível recuperar o conteúdo de "+pag.getChave().replaceAll(" ", "_"));
			return new ArrayList<Link>();
		}
		return scanLinks(pag);
	}
	
	private static List<Link> scanLinks(Pagina pagina) throws Exception{
		
		String text = pagina.getConteudo(); 
		String[] links = text.split("\\[\\[");
		List<Link> listaLink = new ArrayList<Link>();
		//Pula o primeiro termo do array porque ele não contém nenhum link
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
			link.setPaginaDestino(new Pagina()); //tenho fé que conseguiremos catar no banco.
			link.setLinkName(linkName);
			link.setAlias(alias);
			//TODO nao verifica se é strong link ainda
			link.setStrongLink(true);
			listaLink.add(link);
			
		}
		return listaLink;
	}
}
