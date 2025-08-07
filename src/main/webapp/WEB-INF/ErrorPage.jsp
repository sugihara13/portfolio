<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
	<jsp:include page="/WEB-INF/headCommon.jsp"></jsp:include>
	<title>Error | WireBoutique</title>
</head>
<body>
    	<jsp:include page="header.jsp"></jsp:include>
    	
    	<main class="vertical-flexbox">
    		<div class="container vertical-flexbox">
    			<h2 class="page-title">Error !</h2>
    			<a href="<%= request.getContextPath() %>/Index">WireBoutique</a>
    		</div>
    	</main>
    	
        <jsp:include page="footer.jsp"></jsp:include>
	</body>
</html>