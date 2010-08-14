package br.com.wiigle.view.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.PropertyUtils;

public abstract class MBean {

	private ResourceBundle mensagens;
	
	public static final String SUCESSO = "success";
	public static final String FALHA = "failure";
	
	public MBean() {
		
		carregarMensagens();
		
	}
	
	private void carregarMensagens() {
		HttpSession sessao = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        
        //se ainda nao armazenou na sessao o arquivo de mensagens
        if (sessao.getAttribute("mensagens") == null) {
        	
        	//le o arquivo de mensagens e guarda-o na sessao
        	mensagens = ResourceBundle.getBundle("br.com.projetofinal.view.resources.Mensagens");
        	sessao.setAttribute("mensagens", mensagens);
        
        //se o arquivo ja esta na sessao, recupera-o
        } else {
        	mensagens = (ResourceBundle) sessao.getAttribute("mensagens");
        }
	}
	
	public static <T> List<SelectItem> listToSelectItemsObject(List<T> lista, String nomeMetodo){
		
		List<SelectItem> selectItems = new ArrayList<SelectItem>();
		
		if (lista != null) {
			
			String valor;
			
			//para cada objeto existente na lista passada como parametro
			for (T objeto: lista) {
				try {
					valor = PropertyUtils.getProperty(objeto, nomeMetodo).toString();
					
				// se nao conseguiu pegar esse valor
				} catch (Exception e) {
					
					//busca o valor no toString do objeto
					valor = objeto.toString();
				} 
				selectItems.add(new SelectItem(objeto, valor));
				
			}
		}
		
		return selectItems;
	}
	
	public ResourceBundle getMensagens() {
		return mensagens;
	}

}
