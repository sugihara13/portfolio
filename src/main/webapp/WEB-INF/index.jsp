<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>


<%@ page import ="java.util.List" %>
<%@ page import ="wireboutique.beans.ProductBean" %>
<%	List<ProductBean> products =	(List<ProductBean>)request.getAttribute("products"); %>

<!DOCTYPE html>
<html>
    <head>
        <title>WireBoutique</title>
        <jsp:include page="headCommon.jsp"></jsp:include>
    </head>

    <body>
    	<!-- header -->
    	<jsp:include page="header.jsp"></jsp:include>

        <main class="vertical-flexbox">
        
        
        	<form id="" class="index-search-form" action="ProductList" method="POST">
						<input id="product-list-searchbox" type="search" name="keywords" minlength="1" maxlength="200" value="${keywords}">
						<button class="button button-style" type="submit" name="product-list-action" value="search">search</button>
			</form>

            <h2 class="page-title">WireBoutique</h2>

            <div class="container vertical-flexbox">
                <h3>Products</h3>
                <div class="card-wrapper">
                    <c:forEach var="product" items="${ products}">
                    	<div class="card-style vertical-flexbox">
                    		<a href="Product?Id=${product.id }"><img class="product-pannel-img" src="img?type=product&id=${product.id }&item=pannel" alt="${product.name } image"></a>
                        	<h3><c:out value="${product.name}"></c:out></h3>
                        	<p>caption</p>
                        	<p><c:out value="${product.IncTaxPrice() }"></c:out></p>
                    	</div>
                    </c:forEach>
                </div>
            </div>
            
            <a class="button-style-blue" href="ProductList">View  All Product</a>
        </main>
		
		<!-- footer -->
        <jsp:include page="footer.jsp"></jsp:include>
    </body>
</html>