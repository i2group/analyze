<!-- 
/*******************************************************************************
 * Copyright (c) 2014, 2019 IBM Corp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corp - initial API and implementation and initial documentation
 ******************************************************************************/
 -->
<%@page import="com.example.ExternalDataRetrieval"%>
<%@page import="java.util.Map.Entry,java.util.Set,java.util.Map" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<style type="text/css">
h1 { 
	background-color: #D8D8D8;
	border: 1px solid black;
	padding: 10px; 
	width: 220px;
	}

.property {
		margin: 5px;
	}

.property-value {
	padding-left: 5px;
	position: absolute;
	left: 150px;
	width: 300px;
	border: 1px solid black;
	background-color: #EEEEEE;
	}
	
</style>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Subsetting Example</title>
</head>
<body>
<%
	final String itemId = request.getParameter("id");
	final String source = request.getParameter("source");

	final ExternalDataRetrieval edr = new ExternalDataRetrieval(itemId, source);
%>

<h1>Item Properties</h1>

<%
	for (Entry<String, String> entry : edr.getItemProperties().entrySet())
	{
%>
		<div class="property">
			<span class="property-key"><%= entry.getKey() %>: </span>
			<span class="property-value"><%= entry.getValue() %></span>
		</div>
<% 		
	}
%>


</body>
</html>
