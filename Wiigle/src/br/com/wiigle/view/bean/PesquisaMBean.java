package br.com.wiigle.view.bean;

import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import br.com.wiigle.model.Termo;
import br.com.wiigle.model.TermoDAO;

public class PesquisaMBean {

	private static final int VALIDADE_EM_DIAS = 5;
	
	private String textoConsulta;
	
	@PostConstruct
	public void inicializar(){
		System.out.println("Passou aqui");
	}

	public void desambiguar(){
		//Recupera o termo digitado
		String chave = textoConsulta;
		
		try{
			//Verifica se já existe um termo no banco com esta chave
			Termo termo = null; //TermoDAO.recuperarTermo(chave);
			if(termo==null || termo.getDataExpiracao().before(new Date())){
				//Caso não haja o termo ou ele já esteja expirado
				//recuperá-lo fazendo requisição à wikipedia
				
				URL wiki = new URL("http://en.wikipedia.org/wiki/Special:Export/"+chave+"_(disambiguation)");
				URLConnection wc = wiki.openConnection();
				String desambiguacoes = getDesambiguacoes(wc.getInputStream());
				List<String> colDesambiguacoes = processaDesambiguacoes(desambiguacoes);
				if(!colDesambiguacoes.isEmpty()){
					//Prepara o termo para salvá-lo no banco
					if(termo == null)
						termo = new Termo();
					termo.setChave(chave);
					termo.setDesambiguacoes(colDesambiguacoes);
					Calendar c = Calendar.getInstance();
					c.add(Calendar.DAY_OF_MONTH, VALIDADE_EM_DIAS);
					termo.setDataExpiracao(c.getTime());
					
					//Salva o termo no banco
					//TermoDAO.insertOrUpdateTermo(termo);
				}
				
			}
			
			
		}catch(Exception e){
			e.printStackTrace();
		}

	}
	
	private String getDesambiguacoes(InputStream stream){
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
	
	private List<String> processaDesambiguacoes(String desambiguacoes){
		String[] linhas = desambiguacoes.split("\n");
		List<String> retorno = new ArrayList<String>();
		for (String linha : linhas) {
			if(!linha.startsWith("* [["))
				continue;//Não é uma desambiguação
			
			//Removendo o começo da linha
			linha = linha.replaceFirst("\\* \\[\\[", "");
			
			//Removendo o resto da linha, para ficar apenas com os termos de desambiguação
			linha = linha.substring(0, linha.indexOf("]]"));
			
			if(linha.contains("|"))
				linha = linha.substring(0, linha.indexOf("|"));
			
			retorno.add(linha.trim());
		}
		return retorno;
	}
	
	
	public void setTextoConsulta(String textoConsulta) {
		this.textoConsulta = textoConsulta;
	}

	public String getTextoConsulta() {
		return textoConsulta;
	}
	
}
