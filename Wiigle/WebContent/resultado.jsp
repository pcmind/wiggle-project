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
		  			
		  			<div class="topoResultado"></div>
		  			
		  			<div class="centro">
			  			<h:outputLabel value="#{labels.dominioTexto}"/>
			  			<rich:dataTable value="#{Pesquisa.listaResultado}"
			  				var="objeto"
			  				id="tabelaResultadoId">
			  				
			  			<h:column>
			  				<h:selectBooleanCheckbox value="#{objeto.selecionado}"/>
			  				<h:outputText value="#{objeto.termo}" />
			  			</h:column>
			  			
			  			<h:column>
			  				<h:outputText value="#{objeto.descricao}" />
			  			</h:column>
			  				
		  			
		  			
		  			
		  			
		  				</rich:dataTable>
		  			</div>
					
					<div class="centro">
					<h:commandButton action= "#{Pesquisa.redirecionaResultadoGoogle}" 
										 value="#{botoes.pesquisaGoogle}"
										 id="botaoUploadId"
										 type="submit"
										 styleClass="botao"
										 ignoreDupResponses="true"/>
		  			<div>	
					
	
				</a4j:form>																					
																			
			</ui:define>			
		</ui:composition>
	</f:view>
</html>

