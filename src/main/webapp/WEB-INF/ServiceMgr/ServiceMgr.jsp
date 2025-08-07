<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>    
    
<!DOCTYPE html>
<html>
	<head>
		<title>WireBoutique ServiceManager</title>
		<jsp:include page="/WEB-INF/ServiceMgr/headCommon.jsp"></jsp:include>
	</head>
	<body>
	<jsp:include page="/WEB-INF/ServiceMgr/header.jsp"></jsp:include>
	
	<main class="vertical-flexbox">
		<div class="service-mgr-index-heading">
			<h2 class="page-title">WireBoutique</h2>
			<p class="page-title">ServiceManager</p>
		</div>
		<div class="container vertical-flexbox">
			<section class="service-mgr-index-menu">
				<h3 class="section-heading">Roles</h3>
				<div id="" class="card-wrapper">
					<a href="ServiceMgr/RoleManager">		
						<div class="card-style vertical-flexbox">
							<h3>RoleManager</h3>
						</div>
					</a>
        		</div>
			</section>
			
			<section class="service-mgr-index-menu">
				<h3 class="section-heading">Product</h3>
			
				<div id="" class="card-wrapper">
					<a href="ServiceMgr/ProductList">
						<div class="card-style vertical-flexbox">						
							<h3>ProductList</h3>
						</div>
					</a>
				
					<a href="ServiceMgr/ProductRegistration">
						<div class="card-style vertical-flexbox">						
							<h3>Registration</h3>	
						</div>
					</a>
					
					<a href="">
						<div class="card-style vertical-flexbox">
	                   		<h3>none</h3>           
	        			</div>
	        		</a>
				</div>
			</section>
		</div>
	</main>
	
	<jsp:include page="/WEB-INF/footer.jsp"></jsp:include>
</body>
</html>