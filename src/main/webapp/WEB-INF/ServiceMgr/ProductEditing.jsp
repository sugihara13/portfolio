<%@page import="wireboutique.beans.TaxBean"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>    

<%@ page import ="java.util.Map" %>
<%@ page import ="java.util.List" %>

<%@ page import ="wireboutique.beans.ProductBean" %>
<%@ page import ="wireboutique.beans.TaxBean" %>

<%	List<String> msgLines =	(List<String>)request.getAttribute("msgLines"); %>

<%	ProductBean	product =	(ProductBean)request.getAttribute("product"); %>

<%	Map<Integer, String> manufacturers =	(Map<Integer, String>)request.getAttribute("manufacturers"); %>
<%	Map<Integer, String> catedories =	(Map<Integer, String>)request.getAttribute("categories"); %>
<%	List<TaxBean> taxCategories =	(List<TaxBean>)request.getAttribute("taxCategories");%>

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
    			</c:otherwise>
    		</c:choose>
    		
			<c:forEach var="msg" items="${msgLines }">
				<p><c:out value="${msg }"></c:out></p>
			</c:forEach>
			
			<div class="form-wrapper vertical-flexbox">
			<form id="role-editing-form" class="" action="ProductRegistration" method="POST" enctype="multipart/form-data">
				<c:choose>
   					<c:when test="${isAddProduct == true}">
    					<h3 class="form-heading">New product</h3>
    				</c:when>
    				<c:otherwise>
    					<h3 class="form-heading"><c:out value="${product.name }"></c:out></h3>
    					<label class="form-item">
							<p>Product ID</p>
							<p><c:out value="${product.id }"></c:out></p>
							<input type="hidden" name="id" value="${product.id }">
						</label>
    				</c:otherwise>
    			</c:choose>
				
				<label class="form-item">
					<p>Name</p>
					<input id="name" type="text" name="name" minlength="2" value="${product.name } " required>
				</label>
				<label class="form-item">
					<p>Manufacturer</p>
					<select id="manufacturer" name="manufacturer" required>
	  					<option value="">----choose manufacturer----</option>
	  						
	  					<c:forEach var="manufacturer" items="${ manufacturers}">
	  						<c:if test="${manufacturer.getValue() == product.manufacturer }">
	                			<option value="${manufacturer.getKey() }" selected><c:out value="${manufacturer.getValue() }"></c:out></option>
	                		</c:if>
	                		<c:if test="${manufacturer.getValue() != product.manufacturer }">
	                			<option value="${manufacturer.getKey() }"><c:out value="${manufacturer.getValue() }"></c:out></option>
	                		</c:if>
	                	</c:forEach>
					</select>
				</label>
				<label class="form-item">
					<p>Category</p>
					<select id="category" name="category" required>
	  					<option value="">----choose category----</option>
	  						
	  					<c:forEach var="category" items="${ categories}">
	  						<c:if test="${category.getValue() == product.category }">
	                			<option value="${category.getKey() }" selected><c:out value="${category.getValue() }"></c:out></option>
	                		</c:if>
	                		<c:if test="${category.getValue() != product.category }">
	                			<option value="${category.getKey() }" ><c:out value="${category.getValue() }"></c:out></option>
	                		</c:if>
	                	</c:forEach>
					</select>
				</label>	
				
				<h4 class="form-heading">Price</h4>
				<label class="form-item">
					<p>Listprice</p>
					<input id="list-price" type="number" name="list-price" min="0" max="99999999.99" value="${product.listPrice }" step="0.01" required>
				</label>
				<label class="form-item">
					<p>Tax category</p>
					<select id="tax-category" name="tax-category" required>
	  					<option value="">----choose tax category----</option>
	  						
	  					<c:forEach var="tax" items="${ taxCategories}">
	  						<c:if test="${tax.id == product.taxCategory.id }">
	                			<option value="${tax.id }" selected><c:out value="${tax.category }"></c:out></option>
	                		</c:if>
	                		<c:if test="${tax.id != product.taxCategory.id }">
	                			<option value="${tax.id }"><c:out value="${tax.category }"></c:out></option>
	                		</c:if>
	                	</c:forEach>
					</select>
				</label>
				
				<h4 class="form-heading">Release date</h4>
				<label class="form-item">
					<p>Date</p>
					<input id="release-date" type="date" name="release-date" min="2001-01-01" max="2200-12-31" value="${product.releaseDate.toLocalDate()  }" required>
				</label>
				
				<c:if test="${isAddProduct == true }">
					<h4 class="form-heading">Stock</h4>
					<label class="form-item">
						<p>Initial Stock</p>
						<input id="initial-stock" type="number" name="initial-stock" min="0">
					</label>
					<label class="form-item">
						<p>Unlimited</p>
						<input id="stock-limit" type="checkbox" name="stock-limit" value="unlimited">
					</label>
					
					<h4 class="form-heading">Content Resouces</h4>
					<label class="form-item">
						<p>Main Image</p>
						<p>max size 2048*2048 / 15MB</p>
						<input id="main-image" type="file" name="main-image" accept="image/jpeg">
					</label>
				</c:if>
				
				<h4 class="form-heading">Showing Setting</h4>
				<label class="form-item">
					<p>Public</p>
					<c:if test="${product.isPublic == true }">
						<input id="public-setting" type="radio" name="public-setting" value="true" checked>
					</c:if>
					<c:if test="${product.isPublic != true }">
						<input id="public-setting" type="radio" name="public-setting" value="true">
					</c:if>
				</label>
				<label class="form-item">
					<p>Private</p>
					<c:if test="${product.isPublic == false }">
						<input id="public-setting" type="radio" name="public-setting" value="false" checked required>
					</c:if>
					<c:if test="${product.isPublic != false }">
						<input id="public-setting" type="radio" name="public-setting" value="false" required>
					</c:if>
				</label>
				
				<input type="hidden" name="isAddProduct" value="${isAddProduct }">
				<button class="form-confirm-button button-style-blue" type="submit" name="product-registration-action" value="confirm">Confirm</button>
			</form>
			</div>
		</div>
	</main>
	
	<jsp:include page="/WEB-INF/footer.jsp"></jsp:include>
</body>
</html>