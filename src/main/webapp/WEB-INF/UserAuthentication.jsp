<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<% String errorMsg = (String)request.getAttribute("errorMsg");%>

<!DOCTYPE html>
<html>
<head>
	<jsp:include page="headCommon.jsp"></jsp:include>
	<title>WireBoutique</title>
</head>
	<body>
    	<!-- header -->
    	<jsp:include page="header.jsp"></jsp:include>
    	
    	<main class="vertical-flexbox">
    		<div class="container vertical-flexbox">
    			<h2 class="page-title">Login</h2>
    			
    			<div class="form-wrapper">
    				<form id="user-authentication" class="registration-form" action="UserAuthentication" method="POST">
    					<c:if test="${not empty errorMsg}">
    						<p class="error-msg"><c:out value="${errorMsg }"></c:out></p>
    					</c:if>
    					
						<label>
							<p>User ID</p>
							<input id="userid" type="text" name="userid" required minlength="8" maxlength="32" pattern="^[\w]{8,32}">
						</label>
						<label>
							<p>Password</p>
							<input id="password" type="password" name="password" required minlength="8" maxlength="32" pattern="^[\w\!-\/:-@\[-`\&#123;-~]{8,32}">
						</label>
						
						<button class="button button-style" type="submit">Login</button>
						
						<a class="button button-style-blue" href="UserRegistration">User Registration</a>
					</form>
    			</div>
    		</div>
    	</main>
    	
    	<!-- footer -->
        <jsp:include page="footer.jsp"></jsp:include>
	</body>
</html>