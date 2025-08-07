
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@page import ="wireboutique.beans.CartBean"%>
<%@page import ="wireboutique.bo.Payment.PaymentMethod"%>
<%@page import ="wireboutique.beans.ProductBean" %>

<%	CartBean cart =	(CartBean)request.getAttribute("cart"); %>
<%	PaymentMethod PaymentMethod =	(PaymentMethod)request.getAttribute("PaymentMethod"); %>

<!DOCTYPE html>
<html>
<head>
	<jsp:include page="headCommon.jsp"></jsp:include>
	<title>Payment Confirm | Wireboutique</title>
</head>
	<body>
    	<!-- header -->
    	<jsp:include page="header.jsp"></jsp:include>
    	
    	<main class="vertical-flexbox">
    		<h2 class="page-title">Order</h2>
    		
    		<div class="container vertical-flexbox">
				<table class="table-style-list">
                <tbody>
                  	<tr>
                    	<th scope="row">Purchaser</th>
                    	<td><c:out value="${purchaser }"></c:out></td>
                  	</tr>
                  	<tr>
                    	<th scope="row">Payment Method</th>
                    	<td><c:out value="${PaymentMethod }"></c:out></td>
                  	</tr>
                </tbody>
              	</table>
    		</div>
    		
    		<div class="container vertical-flexbox">
            	<table class="table-style-list">
                <caption>Products</caption>
                <thead>
                  <tr>
                    <th scope="col">product</th>
                    <th scope="col">quanity</th>
                    <th scope="col">price</th>
                  </tr>
                </thead>
                <tbody>
                  <c:forEach var="product" items="${ cart}">
                  	<tr>
                    	<th scope="row"><c:out value="${product.getKey().name }"></c:out></th>
                    	<td><c:out value="${product.getValue() }"></c:out></td>
                    	<td><c:out value="${cart.value(product.getKey()) }"></c:out></td>
                  	</tr>
                  </c:forEach>
                </tbody>
                <tfoot>
                  <tr>
                    <th class="total-heading" scope="row" colspan="2">Total</th>
                    <td class="total-price"><c:out value="${cart.total() }"></c:out></td>
                  </tr>
                </tfoot>
              </table>			
			</div>
			
			<form action="Checkout" method="POST">
					<button class="button-style" type="submit" name=checkout value="order">checkout</button>
    		</form>
    	</main>
    	
    	<!-- footer -->
        <jsp:include page="footer.jsp"></jsp:include>
	</body>
</html>