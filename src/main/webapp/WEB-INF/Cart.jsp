<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%@page import="wireboutique.beans.CartBean"%>
<%@ page import ="java.util.List" %>
<%@ page import ="java.util.Map" %>
<%@ page import ="wireboutique.beans.ProductBean" %>
<%	CartBean cart =	(CartBean)request.getAttribute("cart"); %>
<%	Map<String,String> ivProducts =	(Map<String,String>)request.getAttribute("ivProducts"); %>

<!DOCTYPE html>
<html>
	<head>
	<jsp:include page="headCommon.jsp"></jsp:include>
	<title>Cart | WireBoutique</title>
</head>
<body>

	<jsp:include page="header.jsp"></jsp:include>

	<main class="vertical-flexbox">
		<div class="container vertical-flexbox">
			<h2 class="page-title">Cart</h2>
		
			<c:if test="${not empty ivProducts}">
				<div class="msg-wrapper">
					<c:forEach var="ivproduct" items="${ ivProducts.entrySet()}">
						<p class="error-msg"><c:out value="${ivproduct.getValue() }"></c:out></p>
					</c:forEach>
					<p>could be not checkout.</p>
					<p>please check your cart inner.</p>
				</div>
			</c:if>
			
			
			<p>Products (${fn:length(cart)})</p>
			<div class="card-wrapper">
				<c:forEach var="product" items="${ cart.entrySet()}">
					<div class="card-style vertical-flexbox">
						<a href="Product?Id=${product.getKey().id }"><img class="product-pannel-img" src="img?type=product&id=${product.getKey().id }&item=pannel" alt="${product.getKey().name } image"></a>
                    	<h3><a href="Product?Id=${product.getKey().id }"><c:out value="${product.getKey().name}"></c:out></a></h3>
                    	<p><c:out value="${product.getKey().IncTaxPrice() }"></c:out></p>
						
                    	<form action="Cart" method="POST">
							<button class="button-style-red" type="submit" name="remove" value="${product.getKey().id }">remove</button>
                    	</form>
					</div>
            	</c:forEach>
            </div>
            
            <p class="cart-total">Total <span class="cart-total-price"><c:out value="${cart.total() }"></c:out></span></p>
		</div>

		<div class="container vertical-flexbox">
			<c:choose>
				<c:when test="${empty cart || cart.size() <= 0}">
					<button class="button-style-grayout">checkout</button>
				</c:when>
				<c:when test="${not empty ivProducts}">
					<button class="button-style-grayout">checkout</button>
				</c:when>
				<c:otherwise>
					<form action="Checkout" method="POST">
						<button class="button-style" type="submit" name="checkout" value="checkout">checkout</button>
					</form>
				</c:otherwise>
			</c:choose>
			
			<form action="Cart" method="POST">
				<button class="button-style-red" type="submit" name="clear" value="clear">Clear cart</button>
			</form>
		</div>
	</main>

	<jsp:include page="footer.jsp"></jsp:include>
</body>
</html>