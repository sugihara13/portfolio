<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import ="wireboutique.beans.UserBean" %>
    
<%
	//requestscope
	UserBean user =	(UserBean)session.getAttribute("user");
%>
<header>
	<div class="header-inner">
   		<h1><a href="WireBoutique">WireBoutique</a></h1>
        <nav class="nav-bar">
        	<a href="Cart" class="nav-button" id="cart-button"></a>
        	
            <input type="checkbox" id="nav-show-ctrl">
            <label Id="nav-show-button" class="nav-button" for="nav-show-ctrl"></label>
            <nav class="header-nav">
                <ul>
                    <li>
                    	<c:choose>
                    		<c:when test="${empty user}">
                    			<a href="/WireBoutique/UserAuthentication">Login</a>
                    		</c:when>
                    		<c:otherwise>
                    			<c:out value="${user.id}"></c:out>
                    		</c:otherwise>
                    	</c:choose>
                    </li>
    
                    <c:if test="${not empty user}">
                    	<li><a href="/WireBoutique/Logout">Logout</a></li>
                    </c:if>
                </ul>
            </nav>     
       </nav>
	</div>
</header>