<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%@ page import ="java.time.format.DateTimeFormatter" %>
<%@ page import ="wireboutique.beans.ProductBean" %>
    
<%	ProductBean product =	(ProductBean)request.getAttribute("product");%>
<%	boolean isValidAddCart =	(boolean)request.getAttribute("isValidAddCart");%>

<!DOCTYPE html>
<html>
<head>
	<jsp:include page="headCommon.jsp"></jsp:include>
	<!-- product name -->
	<title>${ product.name } | WireBoutique</title>
</head>
<body>
	<jsp:include page="header.jsp"></jsp:include>
	
	<main class="vertical-flexbox">
		<!-- <include category bar>-->
		
		<div id="product" class="container">
			<h2 class="product-title"><c:out value="${product.name}"></c:out></h2>
			
			<div class="product-inner">
				<div class="product-data-wrapper">
					<p class="product-data">name: <c:out value="${product.name}"></c:out></p>
					<ul>
						<li class="product-data">category: <c:out value="${product.category}"></c:out></li>
						<li class="product-data">release:  <c:out value="${product.releaseDate.format(DateTimeFormatter.ofPattern('yyyy/MM/dd'))}"></c:out></li>
						<li class="product-data">ID: <c:out value="${product.id}"></c:out></li>
						<li class="product-data">manufacturer: <c:out value="${product.manufacturer}"></c:out></li>	
					</ul>
				</div>
				<div class="product-pannel">
					<img class="product-pannel-img" src="img?type=product&id=${product.id }&item=pannel" alt="${product.name } image"></a>
					<p>Price</p>
					<p class="product-pannel-price"><c:out value="${product.IncTaxPrice() }"></c:out>(Inc tax)</p>
					
					<c:choose>
						<c:when test="${isValidAddCart == true}">
							<form action="Product?Id=${product.id }" method="POST">
								<button class="button-style" type="submit" name="addcart" value="${product.id }">AddCart</button>
							</form>
						</c:when>

						<c:when test="${not empty product.stock && product.stock < 1}">
							<button class="button-style-grayout">Out of stock</button>
						</c:when>

						<c:otherwise>
							<button class="button-style-grayout">AddCart</button>
						</c:otherwise>
					</c:choose>
				</div>
			</div>
		</div>
		
		<section id="product-overview" class="container">
			<h2>overview</h2>
			<h3>details</h3>
		</section>
		
		<section id="" class="container">
			<h2>section</h2>
			<p>caption</p>
		</section>
	</main>
	
	<jsp:include page="footer.jsp"></jsp:include>
	
</body>
</html>