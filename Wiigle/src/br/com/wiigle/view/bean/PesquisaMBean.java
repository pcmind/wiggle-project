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
	
	// O caminho físico do arquivo que será importado para o sistema
    // Este arquivo ficará em uma pasta temporária no servidor
    private String caminhoArquivo;

    
    // Número máximo de arquivos que atualmente o sistema permite importar
    // simultaneamente
    private int uploadsAvailable = 1;

    // Flag que indica se o componente FileUpload fará o upload automaticamente,
    // sem a necessidade do usuário clicar em "ENVIAR"
    private boolean autoUpload = false;
    
    //variáveis referentes ao suggestionbox
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
	      //TODO aqui entrará o código de auto 
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
	 * ação do botão "upload arquivo"
	 * @return
	 */
	public String upload(){
		setRenderizaUpload(true);
		
		return MBean.SUCESSO;
	}
	
	/**
	 * Ação do botão "Cancelar"
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
	 * Ação do botão "Desambiguação"
	 */
	public void desambiguar(){
		
		manterPesquisa.desambiguacao(consulta);
	}
	
	 /**
     * Listener utilizado pelo componente FileUpload que é acionado depois que o
     * arquivo é enviado.
     * 
     * Utilizado para informar ao MBean a localização do arquivo no servidor.
     */
    public void listener(UploadEvent event) {
        // Um item contém o arquivo importado e seu ContentType
        UploadItem item = event.getUploadItem();

        /*
         * Quando o txt é importada, é criada uma cópia como arquivo
         * temporário em uma pasta temporária no servidor.
         * 
         * O filtro que define se será criado um arquivo temporário ou se o
         * arquivo importado será carregado apenas na memória RAM do servidor
         * está definido no web.xml. Para createTempFiles = true, será criado um
         * arquivo temporário. Para createTempFiles = false, o arquivo será
         * carregado na memória como um byte[].
         * 
         * O método isTempFile verifica se é arquivo temporário ou não.
         */
        if (item.isTempFile()) { // Arquivo temporário
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
     * Retorna o número máximo de arquivos que atualmente o sistema permite
     * importar simultaneamente
     * 
     * @return O número máximo de arquivos
     */
    public int getUploadsAvailable() {
        return uploadsAvailable;
    }   
  
    /**
     * Define o número máximo de arquivos que atualmente o sistema permite
     * importar simultaneamente
     * 
     * @param uploadsAvailable
     *            O número de arquivos
     */
    public void setUploadsAvailable(int uploadsAvailable) {
        this.uploadsAvailable = uploadsAvailable;
    }   
    
    /**
     * Verifica se o arquivo será enviado automaticamente ao ser escolhido.
     * 
     * @return se o arquivo é enviado automaticamente
     */
    public boolean isAutoUpload() {   
        return autoUpload;   
    }   
  
    /**
     * Define se o arquivo será enviado automaticamente ao ser escolhido.
     * 
     * @param autoUpload se o arquivo é enviado automaticamente
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
