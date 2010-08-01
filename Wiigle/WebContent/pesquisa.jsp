<?xml version="1.0" encoding="ISO-8859-1"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html 	xmlns="http://www.w3.org/1999/xhtml"
		xmlns:ui="http://java.sun.com/jsf/facelets"
		xmlns:h="http://java.sun.com/jsf/html"
		xmlns:rich="http://richfaces.org/rich"
		xmlns:a4j="http://richfaces.org/a4j"
		xmlns:f="http://java.sun.com/jsf/core">
		
	<f:view>
	
		<ui:composition template="templates/default.html">
			
			<ui:define name="titulo">
				<h:outputText value="#{titulos.wiigle_navegador}" />
			</ui:define>
			
			<ui:define name="conteudo">
		  
				<a4j:form id="formulario">
					
					<ui:include src="main/messages.html"/>
					
		  			<a4j:keepAlive beanName="Pesquisa"/>
		  			
		  			<div>
						<h:inputText value="#{Pesquisa.textoConsulta}"
										id="nomeId"
										styleClass="larg300"/>
					</div>
					
					<a4j:commandButton action="#{Pesquisa.desambiguar}" 
														value="#{botoes.desambiguacao}"
														id="botaoSalvarId"
														type="submit"
														ignoreDupResponses="true"/>
		  			

	
				</a4j:form>																					
																			
			</ui:define>			
		</ui:composition>
	</f:view>
</html>



