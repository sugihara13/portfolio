<%@page import="wireboutique.beans.TaxBean"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>    

<%@ page import ="java.util.Map" %>
<%@ page import ="java.util.List" %>

<%@ page import ="wireboutique.beans.ProductBean" %>
<%@ page import ="wireboutique.beans.TaxBean" %>

<%	List<String> difference = (List<String>)request.getAttribute("difference"); %>
<%	ProductBean	product =	(ProductBean)request.getAttribute("product"); %>
<%	ProductBean	beforeProduct =	(ProductBean)request.getAttribute("beforeProduct"); %>

<%	int	manufacturerId =	(int)request.getAttribute("manufacturerId"); %>
<%	int	categoryId =	(int)request.getAttribute("categoryId"); %>
<%	Map<String,String> uploadFileNames	=	(Map<String,String>)request.getAttribute("uploadFileNames"); %>

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
    			</c:when>
    			<c:otherwise>
    				<h2 class="page-title">Edit Product</h2>
    				<p class="section-title"><c:out value="${beforeProduct.name }"></c:out></p>
    			</c:otherwise>
    		</c:choose>
    			
			<c:forEach var="dmsg" items="${difference }">
				<p><c:out value="${dmsg }"></c:out></p>
			</c:forEach>
			
			<div class="form-wrapper vertical-flexbox">
			<form id="role-editing-form" class="" action="ProductRegistration" method="POST">
				<c:choose>
   					<c:when test="${isAddProduct == true}">
    					<h3 class="">New product</h3>
    				</c:when>
    				<c:otherwise>
    					<h3 class=""><c:out value="${product.name }"></c:out></h3>
    					<label class="form-item">
							<p><c:out value="Product ID: ${product.id }"></c:out></p>
							<input type="hidden" name="id" value="${product.id }">
						</label>
    				</c:otherwise>
    			</c:choose>
				
				<h4 class="form-heading">Name</h4>
				<label class="form-item">
					<p><c:out value="${product.name }"></c:out></p>
					<input type="hidden" name="name" value="${product.name }">
				</label>
				
				<h4 class="form-heading">Manufacturer</h4>
				<label class="form-item">
					<p><c:out value="${product.manufacturer }"></c:out></p>
					<input type="hidden" name="manufacturer" value="${manufacturerId }">
				</label>
				
				<h4 class="form-heading">Category</h4>
				<label class="form-item">
					<p><c:out value="${product.category }"></c:out></p>
					<input type="hidden" name="category" value="${categoryId }">
				</label>	
				
				<h4 class="form-heading">Listprice</h4>
				<label class="form-item">
					<p><c:out value="${product.listPrice }"></c:out></p>
					<input type="hidden" name="list-price" value="${product.listPrice }">
				</label>
				
				<h4 class="form-heading">Tax category</h4>
				<label class="form-item">				
					<p><c:out value="${product.taxCategory.category }"></c:out></p>
					<p><c:out value="rate: ${product.taxRate }"></c:out></p>
					<input type="hidden" name="tax-category" value="${product.taxCategory.id }">
				</label>
				
				<label class="form-item">
					<p>Including tax</p>
					<p><c:out value="${product.IncTaxPrice() }"></c:out></p>
				</label>
				
				<h4 class="form-heading">Release date</h4>
				<label class="form-item">
					<p><c:out value="${product.releaseDate.toLocalDate() }"></c:out></p>
					<input type="hidden" name="release-date" value="${product.releaseDate.toLocalDate() }">
				</label>
				
				<c:if test="${isAddProduct == true }">
					<h4 class="form-heading">Stock</h4>
					<label class="form-item">
						<c:if test="${not empty product.stock }">
							<p>Initial Stock</p>
							<p><c:out value="${product.stock }"></c:out></p>
						</c:if>
						<input type="hidden" name="initial-stock" value="${product.stock }">
						
						<c:if test="${empty product.stock}">
							<p>Unlimited stock</p>
							<input type="hidden" name="stock-limit" value="unlimited">
						</c:if>
					</label>
					
					<h4 class="form-heading">Content Resouces</h4>
					<label class="form-item">
						<p>Main Image</p>
						<p><c:out value="${uploadFileNames.get('pannel') }"></c:out></p>
						<input type="hidden" name="main-image" value="">
					</label>
				</c:if>
				
				<h4 class="form-heading">Showing Setting</h4>
				<label class="form-item">
					<c:if test="${product.isPublic }">
						<p>public</p>
					</c:if>
					<c:if test="${not product.isPublic}">
						<p>private</p>
					</c:if>
					<input type="hidden" name="public-setting" value="${product.isPublic }">
				</label>
				
				<input type="hidden" name="isAddProduct" value="${isAddProduct }">
				<button class="form-confirm-button button-style-blue" type="submit" name="product-registration-action" value="apply">apply</button>
			</form>
			</div>
		</div>
	</main>
	
	<jsp:include page="/WEB-INF/footer.jsp"></jsp:include>
</body>
</html>