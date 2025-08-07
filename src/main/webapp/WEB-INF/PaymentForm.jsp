<%@page import="wireboutique.bo.Payment.PaymentMethod"%>
<%@page import="wireboutique.bo.Payment.PaymentMethod"%>
<%@page import="wireboutique.bo.Payment.PaymentMethod"%>
<%@page import="wireboutique.bo.Payment.PaymentMethod"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ page import ="wireboutique.beans.UserBean" %>
<%@ page import ="wireboutique.bo.Payment.PaymentMethod" %>
    
<%
	UserBean user =	(UserBean)session.getAttribute("user");

	PaymentMethod pmCard = (PaymentMethod)request.getAttribute("pmCard");
	PaymentMethod pmBank = (PaymentMethod)request.getAttribute("pmBank");
%>

<!DOCTYPE html>
<html>
<head>
	<jsp:include page="headCommon.jsp"></jsp:include>
	<title>Payment | Wireboutique</title>
</head>
	<body>
    	<!-- header -->
    	<jsp:include page="header.jsp"></jsp:include>
    	
    	<main class="vertical-flexbox">
    		<div class="container vertical-flexbox">
    			<h2 class="page-title">Paymentform</h2>
    			
    			<div class="form-wrapper">
    				<form id="payment-form" class="registration-form" action="Payment" method="POST">
						<h3 class="form-heading">Purchaser</h3>
						<label  class="form-item">
						<p>User</p>
						<p><c:out value="${user.id}"></c:out></p>
						</label>
						
						<label class="form-item">
							<p>Purchaser</p>
							<input id="purchaser" type="text" name="purchaser" required minlength="4" maxlength="50">
						</label>
						
						<h3 class="form-heading">Payment Method</h3>
						
						<label class="form-item">
							<p>card</p>
							<input type="radio" id="payment-method" name="payment-method" value="${pmCard.toString() }" required/>
						</label>
						
						<label class="form-item">
							<p>bank transfer</p>
							<input type="radio" id="payment-method" name="payment-method" value="${pmBank.toString() }" required/>
						</label>
						
						<button class="button-style" type="submit" name="payment" value="confirm">confirm</button>
					</form>
				</div>
				
    		</div>
    	</main>
    	
    	<!-- footer -->
        <jsp:include page="footer.jsp"></jsp:include>
	</body>
</html>