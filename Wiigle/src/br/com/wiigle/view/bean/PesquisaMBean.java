package br.com.wiigle.view.bean;


import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.model.SelectItem;

import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import br.com.wiigle.control.IManterPesquisa;
import br.com.wiigle.control.ManterPesquisa;
import br.com.wiigle.view.utils.Consulta;
import br.com.wiigle.view.utils.MBean;

public class PesquisaMBean {
	
	IManterPesquisa manterPesquisa;
	
	// O caminho f�sico do arquivo que ser� importado para o sistema
    // Este arquivo ficar� em uma pasta tempor�ria no servidor
    private String caminhoArquivo;

    
    // N�mero m�ximo de arquivos que atualmente o sistema permite importar
    // simultaneamente
    private int uploadsAvailable = 1;

    // Flag que indica se o componente FileUpload far� o upload automaticamente,
    // sem a necessidade do usu�rio clicar em "ENVIAR"
    private boolean autoUpload = false;
    
    //vari�veis referentes ao suggestionbox
    private List<Consulta> listaConsulta = new ArrayList<Consulta>();
	private String currentStateFilterValue;
	private String currentNameFilterValue;
    
    
	private Consulta consulta;
	
	private String textoConsultaTemp;
	
	public boolean renderizaUpload;
	
	
	//quantidade de linhas que aparece na suggestionbox
	private Integer intRows;
	
	@PostConstruct
	public void inicializar(){
		
		consulta = new Consulta();
		intRows = 5;
		renderizaUpload = false;
		manterPesquisa = new ManterPesquisa();
		System.out.println("Passou aqui");
		
		
		/*Consulta consulta = new Consulta();
		consulta.setValor("foca");
		listaConsulta.add(consulta);
		consulta = new Consulta();
		consulta.setValor("foca amestrada");
		listaConsulta.add(consulta);
		consulta = new Consulta();
		consulta.setValor("pica");
		listaConsulta.add(consulta);
		consulta = new Consulta();
		consulta.setValor("picadasGalaxias");
		listaConsulta.add(consulta);*/
	}

	  public List<Consulta> autocomplete(Object suggest) {
	      //TODO aqui entrar� o c�digo de auto 
		  manterPesquisa.desambiguacaoRapida((String) suggest);
		  return null;
		 /* String pref = (String)suggest;
	        List<Consulta> result = new ArrayList<Consulta>();

	        Iterator<Consulta> iterator = getListaConsulta().iterator();
	        while (iterator.hasNext()) {
	        	Consulta elem = ((Consulta) iterator.next());
	            if ((elem.getValor() != null && elem.getValor().toLowerCase().indexOf(pref.toLowerCase()) == 0) || "".equals(pref))
	            {
	                result.add(elem);
	            }
	        }
	        return result; */
	    }
	    
	public void resetFilter() {
		setCurrentNameFilterValue("");
	    setCurrentStateFilterValue("");
	}
	
	/**
	 * a��o do bot�o "upload arquivo"
	 * @return
	 */
	public String upload(){
		setRenderizaUpload(true);
		
		return MBean.SUCESSO;
	}
	
	/**
	 * A��o do bot�o "Cancelar"
	 * @return
	 */
	public String cancelar(){
		setRenderizaUpload(false);
		if(caminhoArquivo!=null){
			File file = new File(caminhoArquivo);
			file.delete();
			consulta.setValor(textoConsultaTemp);
			consulta.setPath(false);
		}
		return MBean.SUCESSO;
	}
	
	
	/**
	 * A��o do bot�o "Desambigua��o"
	 */
	public void desambiguar(){
		
		manterPesquisa.desambiguacao(consulta);
	}
	
	 /**
     * Listener utilizado pelo componente FileUpload que � acionado depois que o
     * arquivo � enviado.
     * 
     * Utilizado para informar ao MBean a localiza��o do arquivo no servidor.
     */
    public void listener(UploadEvent event) {
        // Um item cont�m o arquivo importado e seu ContentType
        UploadItem item = event.getUploadItem();

        /*
         * Quando o txt � importada, � criada uma c�pia como arquivo
         * tempor�rio em uma pasta tempor�ria no servidor.
         * 
         * O filtro que define se ser� criado um arquivo tempor�rio ou se o
         * arquivo importado ser� carregado apenas na mem�ria RAM do servidor
         * est� definido no web.xml. Para createTempFiles = true, ser� criado um
         * arquivo tempor�rio. Para createTempFiles = false, o arquivo ser�
         * carregado na mem�ria como um byte[].
         * 
         * O m�todo isTempFile verifica se � arquivo tempor�rio ou n�o.
         */
        if (item.isTempFile()) { // Arquivo tempor�rio
            File file = item.getFile();
            this.caminhoArquivo = file.getAbsolutePath();
            
            file.deleteOnExit();
        }
        //informa que a consulta leva na verdade o caminho de um arquivo.
        textoConsultaTemp = consulta.getValor();
        consulta.setValor(caminhoArquivo);
        consulta.setPath(true);
    }

    /**
     * Retorna o n�mero m�ximo de arquivos que atualmente o sistema permite
     * importar simultaneamente
     * 
     * @return O n�mero m�ximo de arquivos
     */
    public int getUploadsAvailable() {
        return uploadsAvailable;
    }   
  
    /**
     * Define o n�mero m�ximo de arquivos que atualmente o sistema permite
     * importar simultaneamente
     * 
     * @param uploadsAvailable
     *            O n�mero de arquivos
     */
    public void setUploadsAvailable(int uploadsAvailable) {
        this.uploadsAvailable = uploadsAvailable;
    }   
    
    /**
     * Verifica se o arquivo ser� enviado automaticamente ao ser escolhido.
     * 
     * @return se o arquivo � enviado automaticamente
     */
    public boolean isAutoUpload() {   
        return autoUpload;   
    }   
  
    /**
     * Define se o arquivo ser� enviado automaticamente ao ser escolhido.
     * 
     * @param autoUpload se o arquivo � enviado automaticamente
     */
    public void setAutoUpload(boolean autoUpload) {   
        this.autoUpload = autoUpload;   
    }
	
	public boolean isRenderizaUpload() {
		return renderizaUpload;
	}

	public void setRenderizaUpload(boolean renderizaUpload) {
		this.renderizaUpload = renderizaUpload;
	}
	
	public List<Consulta> getListaConsulta() {
		return listaConsulta;
	}

	    public String getCurrentStateFilterValue() {
	        return currentStateFilterValue;
	    }

	    public void setCurrentStateFilterValue(String currentStateFilterValue) {
	        this.currentStateFilterValue = currentStateFilterValue;
	    }

	    public String getCurrentNameFilterValue() {
	        return currentNameFilterValue;
	    }

	    public void setCurrentNameFilterValue(String currentNameFilterValue) {
	        this.currentNameFilterValue = currentNameFilterValue;
	    }

		public void setIntRows(Integer intRows) {
			this.intRows = intRows;
		}

		public Integer getIntRows() {
			return intRows;
		}
		
		public Consulta getConsulta() {
			return consulta;
		}

		public void setConsulta(Consulta consulta) {
			this.consulta = consulta;
		}
	
}
