<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%	String errorMsg = (String)request.getAttribute("errorMsg"); %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
	<jsp:include page="/WEB-INF/ServiceMgr/headCommon.jsp"></jsp:include>
	<title>Error | WireBoutique</title>
</head>
<body>
    	<jsp:include page="/WEB-INF/ServiceMgr/header.jsp"></jsp:include>
    	
    	<main class="vertical-flexbox">
    		<div class="container vertical-flexbox">
    			<h2 class="page-title">Error !</h2>
    			<p><c:out value="${errorMsg }"></c:out></p>
    			

    		</div>
    	</main>
    	
        <jsp:include page="/WEB-INF/footer.jsp"></jsp:include>
	</body>
</html>