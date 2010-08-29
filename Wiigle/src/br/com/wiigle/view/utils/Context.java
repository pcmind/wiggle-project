package br.com.wiigle.view.utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

public class Context {
	  
    public static final Severity SUCESSO = FacesMessage.SEVERITY_INFO;
    public static final Severity FALHA = FacesMessage.SEVERITY_ERROR;
    public static final Severity AVISO = FacesMessage.SEVERITY_WARN;

    
    /**
     * Adiciona uma mensagem ao contexto, para que seja exibida na tela.
     * 
     * @param mensagem A mensagem a ser exibida
     * @param tipoMensagem O tipo de mensagem (sucesso ou falha). Utilize as constantes da classe.
     * 
     */
    public static void addMessage(String mensagem, Severity tipoMensagem) {
        
        // Passing null for the client ID argument to the FacesContext
        // addMessage() method specifies the message as not belonging
        // to any particular UI component. This will cause the message
        // to be displayed as a general message on the UI        
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(tipoMensagem, mensagem, mensagem));
    }
    
	/**
	 * Recupera um atributo da requisicao.
	 * 
	 * @param attr
	 *            O nome do atributo.
	 * 
	 */
	public static <T> T getAttribute(String attr) {

		HttpServletRequest request = (HttpServletRequest) FacesContext
				.getCurrentInstance().getExternalContext().getRequest();

		Object obj = request.getAttribute(attr);

		/**
		 * Se o atributo nao for atributo da requisicao, tenta acha-lo como
		 * parametro.
		 */
		if (obj == null) {
			obj = request.getParameter(attr);
		}

		return (T) obj;
	}
	
	public static void redirect(String link){
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect(link);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static URL getResource(String recurso){
		try {
			return FacesContext.getCurrentInstance().getExternalContext().getResource(recurso);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}
    
}
