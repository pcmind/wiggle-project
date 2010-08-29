<?xml version="1.0" encoding="ISO-8859-1"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html 	xmlns="http://www.w3.org/1999/xhtml"
		xmlns:ui="http://java.sun.com/jsf/facelets"
		xmlns:h="http://java.sun.com/jsf/html"
		xmlns:rich="http://richfaces.org/rich"
		xmlns:a4j="http://richfaces.org/a4j"
		xmlns:f="http://java.sun.com/jsf/core">
		
	<f:view>
	
			<link href="includes/estilo.css" type="text/css" rel="stylesheet"/>
		<ui:composition template="templates/default.html">
			
			<ui:define name="titulo">
				<h:outputText value="#{titulos.wiigle_navegador}" />
			</ui:define>
			
			<ui:define name="conteudo">
		  
				<a4j:form id="formulario">
					
					<ui:include src="main/messages.html"/>
					
		  			<a4j:keepAlive beanName="Pesquisa"/>
		  			
		  			<div class="topo"></div>
		  			
		  			<div class="centro">
						 <h:inputText value="#{Pesquisa.consulta.valor}" id="pesquisaId" styleClass = "inputPesquisa"
						 rendered = "#{!Pesquisa.renderizaUpload}"/>
		                <rich:suggestionbox id="suggestionBoxId" for="pesquisaId" tokens=",[]"
		                    width="400"
		                    height="300"
		                    suggestionAction="#{Pesquisa.autocomplete}" var="result"
		                    fetchValue="#{result.busca}" rows="#{Pesquisa.intRows}"
		                    nothingLabel="#{mensagens.tenteDesambiguacao}" columnClasses="center"
		                    usingSuggestObjects="true">
		                    
		                    <h:column>
		                        <h:outputText value="#{result.termo}" />
		                    </h:column>
		                    <h:column>
		                        <h:outputText value="#{result.descricao}" style="font-style:italic" />
		                    </h:column>
		                </rich:suggestionbox>
		                
		                
		                	<h:commandButton action= "#{Pesquisa.redirecionaGoogle}" 
								 value="#{botoes.pesquisar}"
								 id="botaoRedirecionaId"
								 type="submit"
								 rendered= "#{!Pesquisa.renderizaUpload}"
								 styleClass="botao"
								 ignoreDupResponses="true"
								 reRender="botaoCancelarId, botaoUploadId, uploadId,pesquisaId, botaoDesambiguacaoId"/>
								 	
							
		  				
		  			
					</div>
					
				<div align="center">
					
					<rich:fileUpload fileUploadListener = "#{Pesquisa.listener}"
									 maxFilesQuantity="#{Pesquisa.uploadsAvailable}"   
                                     id="uploadId"   
                                     immediateUpload="#{Pesquisa.autoUpload}"   
									 acceptedTypes = "txt"
									 listHeight="60px"
									 listWidth="625px"
									 locale="pt_BR"
									 rendered = "#{Pesquisa.renderizaUpload}"
									 style="width: 500px;"/>
					
					</div>
				
					
					<div class="centro">
						<a4j:commandButton action="#{Pesquisa.desambiguar}" 
														value="#{botoes.desambiguacao}"
														id="botaoDesambiguacaoId"
														type="submit"
														styleClass="botao"
														ignoreDupResponses="true"/>
						
						
						<h:commandButton action= "#{Pesquisa.upload}" 
										 value="#{botoes.upload}"
										 id="botaoUploadId"
										 type="submit"
										 rendered= "#{!Pesquisa.renderizaUpload}"
										 styleClass="botao"
										 ignoreDupResponses="true"
										 reRender="botaoCancelarId, botaoUploadId, uploadId,pesquisaId, botaoDesambiguacaoId" />
		  				
		  				<h:commandButton action= "#{Pesquisa.cancelar}" 
										 value="#{botoes.cancelar}"
										 id="botaoCancelarId"
										 type="submit"
										 rendered= "#{Pesquisa.renderizaUpload}"
										 styleClass="botao"
										 ignoreDupResponses="true"
										 reRender="botaoCancelarId, botaoUploadId, uploadId,pesquisaId, botaoDesambiguacaoId" />
		  				
					</div>
	
				</a4j:form>																					
																			
			</ui:define>			
		</ui:composition>
	</f:view>
</html>



