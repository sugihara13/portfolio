<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>  
  
<%@ page import ="java.time.format.DateTimeFormatter" %>
<%@ page import ="wireboutique.beans.ProductBean" %>
<%@ page import ="java.util.List" %>

<%	String msg =	(String)request.getAttribute("msg"); %>
<%	List<ProductBean> products =	(List<ProductBean>)request.getAttribute("users"); %>
<%	Integer pageNum = (Integer)request.getAttribute("pageNum");	%>
<%	Boolean isLastPage = (Boolean)request.getAttribute("isLastPage");	%>

<!DOCTYPE html>
<html>
	<head>
		<title>Product List | WireBoutique</title>
		<jsp:include page="/WEB-INF/ServiceMgr/headCommon.jsp"></jsp:include>
	</head>
	<body>
	<jsp:include page="/WEB-INF/ServiceMgr/header.jsp"></jsp:include>
	
	<main class="vertical-flexbox">
		<div class="container vertical-flexbox">
			
			<h2 class="page-title">Product List</h2>
			<p><c:out value="${msg }"></c:out></p>
			<div class="service-mgr-content vertical-flexbox">
				<p class="section-title">Search product</p>
				
				<form id="" class="" action="ProductList" method="POST">
					<div class="searchbox-wrapper">
						<input id="product-list-searchbox" type="text" name="keywords" required minlength="1" maxlength="200" value="${keywords}">
						<button class="button button-style" type="submit" name="product-list-action" value="search">search</button>
					</div>
				</form>
			</div>
		</div>
		
		<div class="container vertical-flexbox">
			<c:choose>
	            <c:when test="${products.size() gt 0}">
					<h3 class="section-title">List</h3>
					
					<table id="product-list-table" class="table-style-list">
		                <thead>
		                	<tr>
		                  		<th scope="col">Name</th>
		                    	<th scope="col">Id</th>
		                    	<th scope="col">Manufacturer</th>
		                    	<th scope="col">Category</th>
		                    	<th scope="col">ReleaseDate</th>
		                    	<th scope="col">ListPrice</th>
		                    	<th scope="col">Showing Setting</th>
		                    	<th scope="col">Edit data</th>
		                	</tr>
		                </thead>
		                <tbody>
		                	<c:forEach var="product" items="${ products}">
		                  		<tr>
		                    		<th scope="row">
		                    			<img class="product-list-img" src="<%= request.getContextPath() %>/img?type=product&id=${product.id }&item=pannel" alt="${product.name } image">
		                    			<c:out value="${product.name }"></c:out>
		                    		</th>
		                    		<td>
		                    			<c:out value="${product.id }"></c:out>
		                    			<form id="" class="role-list-button" action="ProductList" method="POST">
		                    				<input type="hidden" name="product-id" value="${product.id }">
		                    			</form>
		                    		</td>
		                    		<td>
		                    			<c:out value="${product.manufacturer }"></c:out>
		                    		</td>
		                    		<td>
		                    			<c:out value="${product.category }"></c:out>
		                    		</td>
		                    		<td>
		                    			<c:out value="${product.releaseDate.format(DateTimeFormatter.ofPattern('yyyy/MM/dd'))}"></c:out>
		                    		</td>
		                    		<td>
		                    			<c:out value="${product.listPrice }"></c:out>
		                    		</td>
		                    		<td>
		                    			<c:if test="${product.isPublic == true}">
		                    				<p>Public</p>
		                    			</c:if>
		                    			
		                    			<c:if test="${product.isPublic == false}">
		                    				<p>Private</p>
		                    			</c:if>
		                    		</td>
		                    		
		                    		<td>
			                    		<c:choose>
			                    			<c:when test="${false}">
			                    				<div class="role-list-button">
			                    					<button class="button button-style-grayout">edit</button>
			                    				</div>
			                    			</c:when>
			                    			<c:otherwise>
			                    				<form id="" class="role-list-button" action="ProductEditing" method="POST">
			                    					<input type="hidden" name="product-id" value="${product.id }">
			                    					<button class="button button-style-blue" type="submit" name="product-list-action" value="edit-role">edit</button>
			                    				</form>
			                    			</c:otherwise>
			                    		</c:choose>
		                    		</td>
		                  		</tr>
		                  	
		                	</c:forEach>
		                </tbody>
		        	</table>
        		</c:when>
	            <c:otherwise>
		        	<p>product not found</p>
	            </c:otherwise>
	        </c:choose>
	        
        	<c:choose>
				<c:when test="${pageNum le 1}">
					<p class="button-style-grayout">prev</p>
				</c:when>
				<c:otherwise>
					<form id="" class="" action="ProductList?page=${pageNum - 1 }" method="POST">
						<button class="button-style-blue" type="submit" name="product-list-action" value="prev" rel="prev">prev</button>
						<input type="hidden" name="keywords" value="${keywords }">
					</form>
				</c:otherwise>
			</c:choose>
			
			<c:choose>
				<c:when test="${isLastPage}">
					<p class="button-style-grayout">next</p>
				</c:when>
				<c:otherwise>
					<form id="" class="" action="ProductList?page=${pageNum + 1 }" method="POST">
		            	<button class="button-style" type="submit" name="product-list-action" value="next" rel="next">next</button>
		            	<input type="hidden" name="keywords" value="${keywords }">
		            </form>
            	</c:otherwise>
			</c:choose>
		</div>
	</main>
	
	<jsp:include page="/WEB-INF/footer.jsp"></jsp:include>
</body>
</html>