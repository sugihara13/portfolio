<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>


<%@ page import ="java.util.List" %>
<%@ page import ="wireboutique.beans.ProductBean" %>
<%	List<ProductBean> products =	(List<ProductBean>)request.getAttribute("products"); %>
<%	Integer pageNum = (Integer)request.getAttribute("pageNum");	%>
<%	String keywords = (String)request.getAttribute("keywords");	%>
<%	Boolean isLastPage = (Boolean)request.getAttribute("isLastPage");	%>

<!DOCTYPE html>
<html>
    <head>
        <jsp:include page="headCommon.jsp"></jsp:include>
    	<title>ProductList | WireBoutique</title>
    </head>

    <body>
    	<!-- header -->
    	<jsp:include page="header.jsp"></jsp:include>

        <main class="vertical-flexbox">
        	<h2 class="page-title">ProductList</h2>
        
        	<div class="container vertical-flexbox">
				<form id="" class="searchbox-wrapper" action="ProductList" method="POST">
						<input id="product-list-searchbox" type="search" name="keywords" minlength="1" maxlength="200" value="${keywords}">
						<button class="button button-style" type="submit" name="product-list-action" value="search">search</button>
				</form>
			</div>
		
            <div class="container vertical-flexbox">
            	<c:choose>
	            	<c:when test="${products.size() gt 0}">
		                <h2>Products</h2>
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
                	</c:when>
	            	<c:otherwise>
		                	<p>product not found</p>
	                </c:otherwise>
	            </c:choose>
            </div>

             <c:choose>
				<c:when test="${pageNum le 1}">
					<p class="button-style-grayout">prev</p>
				</c:when>
				<c:otherwise>
					<form id="" class="" action="ProductList?page=${pageNum - 1 }" method="POST">
						<button class="button-style-blue" type="submit" name="product-list-action" value="prev" rel="prev">prev</button>
						<input type="hidden" name="keywords" value="${keywords }">
					</form>
				</c:otherwise>
			</c:choose>
			
			<c:choose>
				<c:when test="${isLastPage}">
					<p class="button-style-grayout">next</p>
				</c:when>
				<c:otherwise>
					<form id="" class="" action="ProductList?page=${pageNum + 1 }" method="POST">
		            	<button class="button-style" type="submit" name="product-list-action" value="next" rel="next">next</button>
		            	<input type="hidden" name="keywords" value="${keywords }">
		            </form>
             	</c:otherwise>
			</c:choose>
        </main>
		
		<!-- footer -->
        <jsp:include page="footer.jsp"></jsp:include>
    </body>
</html>