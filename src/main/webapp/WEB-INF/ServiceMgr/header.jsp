<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import ="wireboutique.beans.AdminUserBean" %>
    
<%
	//requestscope
	AdminUserBean adminUser =	(AdminUserBean)session.getAttribute("adminUser");
%>
<header>
	<div class="header-inner">
   		<h1><a href="<%= request.getContextPath() %>/ServiceMgr">WireBoutique ServiceManager</a></h1>
        <nav class="nav-bar">
            <input type="checkbox" id="nav-show-ctrl">
            <label Id="nav-show-button" class="nav-button" for="nav-show-ctrl"></label>
            <nav class="header-nav">
                <ul>
                    <li>
                    	<c:out value="${adminUser.id}"></c:out>
                    </li>
    
                    <c:if test="${not empty adminUser}">
                    
                    	<li><a href="<%= request.getContextPath() %>/ServiceMgr/Logout">Logout</a></li>
                    
                    </c:if>
                </ul>
            </nav>     
       </nav>
	</div>
</header>