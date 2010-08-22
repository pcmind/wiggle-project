package Teste.Entidades;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * É o termo que foi pré-processado (aplicando-se o algoritmo de Porter)
 * 
 * @author Pedro
 *
 */
public class TermoIndexado {

	//o nome do termo indexado.
	private String chave;
	
	//quantidade de entidades relevantes que referenciam esse termo. 
	//Utilizado também para facilitar o algoritmo de índices invertidos
	private Integer qtdRelevante;
	
	//Melhor guardar apenas o id, pq carregar todas as páginas gera um grande overhead.
	private List<Integer> listaIdPaginas = new ArrayList<Integer>();
	
	private Set<Termo> listaTermo = new HashSet<Termo>();

	public TermoIndexado(){
		qtdRelevante=0;
	}
	
	public void setChave(String chave) {
		this.chave = chave;
	}

	public String getChave() {
		return chave;
	}

	public void setQtdRelevante(Integer qtdRelevante) {
		this.qtdRelevante = qtdRelevante;
	}

	public int getQtdRelevante() {
		return qtdRelevante;
	}

	public List<Integer> getListaIdPaginas() {
		return listaIdPaginas;
	}
	
	public void adicionarIdPagina(Integer idPagina){
		for(Integer id : getListaIdPaginas()){
			if(idPagina.equals(id)){
				return;
			}
		}
		getListaIdPaginas().add(idPagina);
		qtdRelevante++;
	}
	
	public void removerIdPagina(Integer idPagina){
		getListaIdPaginas().remove(idPagina);
		
		qtdRelevante = getListaIdPaginas().size();
	}

	public void setListaTermo(Set<Termo> listaTermo) {
		this.listaTermo = listaTermo;
	}

	public Set<Termo> getListaTermo() {
		return listaTermo;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof TermoIndexado &&  this.getChave().equals(((TermoIndexado) obj).getChave()) ){
			
			return true;
			
		}else{
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return this.getChave().hashCode();
	}
}
