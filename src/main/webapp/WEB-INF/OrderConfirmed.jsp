<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ page import ="java.util.List" %>
<%@ page import ="java.time.format.DateTimeFormatter" %>
<%@ page import ="wireboutique.beans.OrderBean" %>
<%@ page import ="wireboutique.beans.OrderDetailBean" %>

<%	OrderBean order =	(OrderBean)request.getAttribute("order"); %>
<%	List<OrderDetailBean> orderDetails =	(List<OrderDetailBean>)request.getAttribute("orderDetails"); %>
    
<!DOCTYPE html>
<html>
<head>
    <jsp:include page="headCommon.jsp"></jsp:include>

	<title>OrderConfirmed | WireBoutique</title>
</head>
<body>
	<jsp:include page="header.jsp"></jsp:include>
	
	<main class="vertical-flexbox">
		<div class="container vertical-flexbox">
			<h2 class="page-title">OrderConfirmed</h2>
			<p>OrderID :<c:out value="${order.orderId }"></c:out></p>
			
			<table class="table-style-list">
                <caption>Order</caption>
                <tbody>
                  	<tr>
                    	<th scope="row">User</th>
                    	<td><c:out value="${order.userId }"></c:out></td>
                  	</tr>
                  	<tr>
                    	<th scope="row">Purchaser</th>
                    	<td><c:out value="${order.purchaser }"></c:out></td>
                  	</tr>
                  	<tr>
                    	<th scope="row">TotalPrice</th>
                    	<td><c:out value="${order.totalPrice }"></c:out></td>
                  	</tr>
                  	<tr>
                    	<th scope="row">PaymentMethod</th>
                    	<td><c:out value="${order.paymentMethod }"></c:out></td>
                  	</tr>
                </tbody>
                <tfoot>
                  <tr>
                    <th scope="row">Date</th>
                    <td><c:out value="${order.orderDate.format(DateTimeFormatter.ofPattern('yyyy/MM/dd HH:mm:ss')) }"></c:out></td>
                  </tr>
                </tfoot>
            </table>
			
            <table class="table-style-list">
                <caption>details</caption>
                <thead>
                  <tr>
                    <th scope="col">product</th>
                    <th scope="col">quanity</th>
                    <th scope="col">price</th>
                  </tr>
                </thead>
                <tbody>
                  <c:forEach var="detail" items="${ orderDetails}">
                  	<tr>
                    	<th scope="row"><c:out value="${detail.getProductName() }"></c:out></th>
                    	<td><c:out value="${detail.quantity }"></c:out></td>
                    	<td><c:out value="${detail.purchasePrice }"></c:out></td>
                  	</tr>
                  </c:forEach>
                </tbody>
                <tfoot>
                  <tr>
                    <th class="total-heading" scope="row" colspan="2">total</th>
                    <td class="total-price"><c:out value="${order.totalPrice }"></c:out></td>
                  </tr>
                </tfoot>
              </table>			
		</div>
		
		<div class="container vertical-flexbox">
			<a href="Index">WireBoutique</a>
		</div>
	</main>
	
	<jsp:include page="footer.jsp"></jsp:include>
</body>
</html>