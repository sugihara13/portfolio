<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>    

<%@ page import ="wireboutique.beans.AdminUserBean" %>
<%@ page import ="java.util.Map" %>
<%@ page import ="java.util.List" %>

<%	String msg =	(String)request.getAttribute("msg"); %>

<%	String	roleId	=	(String)request.getAttribute("roleId"); %>
<%	String	roleName	=	(String)request.getAttribute("roleName"); %>
<%	Map<String,Boolean> rolePermissions =	(Map<String,Boolean>)request.getAttribute("rolePermissions"); %>

<%	List<String> permNameList = (List<String>)request.getAttribute("permNameList"); %>
<%	List<String> difference = (List<String>)request.getAttribute("difference"); %>

<%	//isAddrole　trueで新規のロールを追加 falseで既存のロールを編集	%>
<% 	Boolean isAddRole =	(Boolean)request.getAttribute("isAddRole"); %>

<!DOCTYPE html>
<html>
	<head>
		<title>Role Edit | WireBoutique</title>
		<jsp:include page="/WEB-INF/ServiceMgr/headCommon.jsp"></jsp:include>
	</head>
	<body>
	<jsp:include page="/WEB-INF/ServiceMgr/header.jsp"></jsp:include>
	
	<main class="vertical-flexbox">
		<div class="container vertical-flexbox">
			<h2 class="page-title">Edit Role</h2>
			<c:forEach var="dmsg" items="${difference }">
				<p><c:out value="${dmsg }"></c:out></p>
			</c:forEach>
			
		
			<div class="form-wrapper vertical-flexbox">
				<form id="role-editing-form" class="" action="RoleEditing" method="POST">
    				<h3 class="form-heading">Role name: <c:out value="${roleName }"></c:out></h3>
    				<c:choose>
    					<c:when test="${isAddRole == true}">
    						<p>New Role</p>
    					</c:when>
    					<c:otherwise>
    						<p><c:out value="RoleID: ${roleId }"></c:out></p>
    						
    						<input type="hidden" name="roleid" value="${roleId }">
    					</c:otherwise>
    				</c:choose>
    				
    				<input type="hidden" name="role-name" value="${roleName }">
					
					<div class="table-wrapper">
					<table class="table-style-list">
						<thead>
							<tr>
								<th scope="col">Permssion</th>
								<th scope="col">value</th>
							</tr>	
						</thead>
						<tbody>
    					<c:forEach var="perm" items="${rolePermissions }">
    						<tr>
    							<th scope="row">
									<label><c:out value="${perm.getKey() }"></c:out></label>
								</th>
								<td>
									<label><c:out value="${perm.getValue() }"></c:out></label>
								</td>
								<input type="hidden" name="${perm.getKey() }" value="${perm.getValue()}">
                			</tr>
                		</c:forEach>
                		</tbody>
    				</table>
    				</div>
    				
					<input type="hidden" name="add-role" value="${isAddRole }">
					<button class="button button-style-blue" type="submit" name="role-editing-action" value="apply">Apply</button>
				</form>
			</div>
		</div>
	</main>
	
	<jsp:include page="/WEB-INF/footer.jsp"></jsp:include>
</body>
</html>