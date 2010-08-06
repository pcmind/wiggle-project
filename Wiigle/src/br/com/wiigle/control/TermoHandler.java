package br.com.wiigle.control;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import br.com.wiigle.model.Pagina;
import br.com.wiigle.model.Termo;
import br.com.wiigle.model.TermoDAO;

public class TermoHandler {
	

	private static final int VALIDADE_EM_DIAS = 5;
	
	
	public synchronized static Termo findByKey(String chave) throws IOException{
		//Verifica se já existe no banco
		Termo termo = TermoDAO.recuperarTermo(chave); 
		//Verifica se é ainda é válido o termo
		if(termo==null || termo.getDataExpiracao().before(new Date())){
			//Nesse caso, montar um novo termo
			if(termo==null){
				termo = new Termo();
				termo.setChave(chave);
			}
			
			//Recuperar desambiguações da palavra
			HashMap<String,String>disambiguations = WikiRequester.getDisambiguations(chave);
			ArrayList<Pagina> paginas = new ArrayList<Pagina>();
			
			//TODO Para cada desambiguação, recuperar e processar os links que aparecem em sua pagina
			for (String desambiguacao : disambiguations.keySet()) {
				Pagina pag = new Pagina();
				pag.setChave(desambiguacao);
				pag.setLinks(WikiRequester.getLinksFromPage(desambiguacao));
				paginas.add(pag);
			}
			
			termo.setDesambiguacoes(paginas);
			
			//Seta a nova validade no termo
			Calendar validade = Calendar.getInstance();
			validade.add(Calendar.DAY_OF_MONTH, VALIDADE_EM_DIAS);
			termo.setDataExpiracao(validade.getTime());
			
			//Insere o termo no banco
			TermoDAO.insertOrUpdateTermo(termo);
		}
		return termo;
	}
}
