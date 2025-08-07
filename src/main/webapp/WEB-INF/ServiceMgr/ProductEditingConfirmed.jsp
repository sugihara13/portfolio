<%@page import="wireboutique.beans.TaxBean"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>    

<%@ page import ="java.util.Map" %>
<%@ page import ="java.util.List" %>

<%@ page import ="wireboutique.beans.ProductBean" %>
<%@ page import ="wireboutique.beans.TaxBean" %>

<%	String msg =	(String)request.getAttribute("msg"); %>

<%	ProductBean	product =	(ProductBean)request.getAttribute("product"); %>

<%	//isAddProduct　trueで新規のproductを追加 falseで既存のproductを編集	%>
<% 	Boolean isAddProduct =	(Boolean)request.getAttribute("isAddProduct"); %>

<!DOCTYPE html>
<html>
	<head>
		<title>Product Registration | WireBoutique</title>
		<jsp:include page="/WEB-INF/ServiceMgr/headCommon.jsp"></jsp:include>
	</head>
<body>
	<jsp:include page="/WEB-INF/ServiceMgr/header.jsp"></jsp:include>
	
	<main class="vertical-flexbox">
		<div class="container vertical-flexbox">
			<c:choose>
   				<c:when test="${isAddProduct == true}">
    				<h2 class="page-title">Product Registration</h2>
    				<p>Registered new product</p>
    			</c:when>
    			<c:otherwise>
    				<h2 class="page-title">Edit Product</h2>
    				<p><c:out value="Edited ${product.name }"></c:out></p>
    				<p><c:out value="ProductID: ${product.id }"></c:out></p>	
    			</c:otherwise>
    		</c:choose>
    		
    		
    		<div class="card-style vertical-flexbox">
    			<a href="<%= request.getContextPath() %>/Product?Id=${product.id }">
            		<h3><c:out value="${product.name}"></c:out></h3>
            	</a>
            	<p>caption</p>
            	<p><c:out value="${product.IncTaxPrice() }"></c:out></p>
            </div>
            
		</div>
		<div class="container vertical-flexbox">
    		<a  class="section-heading" href="Product?Id=${product.id }"><c:out value="${product.name }"></c:out></a>
    		
    		<c:choose>
   				<c:when test="${isAddProduct == true}">
					<a href="<%= request.getContextPath() %>/ServiceMgr/ProductRegistration">Return to ProductRegistration</a>
    			</c:when>
    			<c:otherwise>
					<a href="<%= request.getContextPath() %>/ServiceMgr/ProductList">Return to ProductList</a>
    			</c:otherwise>
    		</c:choose>
		</div>
	</main>
	
	<jsp:include page="/WEB-INF/footer.jsp"></jsp:include>
</body>
</html>