<%@page import="wireboutique.controller.serviceMgr.RoleEditingController.ImmutableRole"%>
<%@page import="wireboutique.controller.serviceMgr.RoleEditingController.ImmutableRole"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>    

<%@ page import ="wireboutique.beans.AdminUserBean" %>
<%@ page import ="java.util.Map" %>
<%@ page import ="java.util.List" %>
<%@ page import ="wireboutique.controller.serviceMgr.RoleEditingController.ImmutableRole" %>

<%	String msg =	(String)request.getAttribute("msg"); %>

<%	Map<String,String> roles =	(Map<String,String>)request.getAttribute("roles"); %>
<%	List<String>	imRoles	=	(List<String>)request.getAttribute("imRoles"); %>
<%	List<AdminUserBean> users =	(List<AdminUserBean>)request.getAttribute("users"); %>
<%	AdminUserBean showUser	=	(AdminUserBean)request.getAttribute("showUser");%>
<!DOCTYPE html>
<html>
	<head>
		<title>Role Manager | WireBoutique</title>
		<jsp:include page="/WEB-INF/ServiceMgr/headCommon.jsp"></jsp:include>
	</head>
	<body>
	<jsp:include page="/WEB-INF/ServiceMgr/header.jsp"></jsp:include>
	
	<main class="vertical-flexbox">
		<div class="container vertical-flexbox">
			
			<h2 class="page-title">Role Manager</h2>
			<p><c:out value="${msg }"></c:out></p>
			<div class="service-mgr-content vertical-flexbox">
				<h3 class="section-title">show user</h3>
				
				<c:if test="${empty showUser }">
					<form id="show-user" class="" action="RoleManager" method="POST">
						<label>
							<p>User ID</p>
							<input id="userid" type="text" name="userid" required minlength="8" maxlength="32" pattern="^[\w]{8,32}">
						</label>
						<button class="button button-style" type="submit" name="show-user-action" value="show">Show</button>
					</form>
				</c:if>
				<!-- show user -->
				<c:if test="${not empty showUser }">
					<section class="service-mgr-content vertical-flexbox">
					<h4 class="section-heading"><c:out value="${showUser.id }"></c:out></h4>
					<p>Role : <c:out value="${showUser.roleName }"></c:out></p>
					
					<label for="role-select" class="section-heading">Assignment</label>
					
						<select form="show-user" name="role" id="role-select" required>
	  						<option value="">----new role----</option>
	  						
	  						<c:forEach var="role" items="${ roles}">
	                			<option value="${role.getKey() }"><c:out value="${role.getValue() }"></c:out></option>
	                		</c:forEach>
						</select>
					<form id="show-user" class="" action="RoleManager" method="POST">
						<input type="hidden" name="userid" value="${showUser.id }">
						<button class="button button-style-blue" type="submit" name="show-user-action" value="change-role">Assign</button>
						<button class="button button-style-red" type="submit" name="show-user-action" value="clear">Clear</button>
					</form>
					
					</section>
				</c:if>
				<!-- --------- -->
			</div>
		</div>
		
		<c:if test="${not empty users }">
			<h3 class="section-title">users</h3>
			<section class="service-mgr-content vertical-flexbox">
			
			<c:forEach var="user" items="${ users}">
				<h4 class="section-heading"><c:out value="${user.id }"></c:out></h4>
				<p>Role : <c:out value="${user.roleName }"></c:out></p>
				
				<label for="role-select" class="section-heading"><p>Assignment</p></label>
				
				<form id="show-user" class="" action="RoleManager" method="POST">
					<select name="role" id="role-select" required>
  						<option value="">----new role----</option>
  						
  						<c:forEach var="role" items="${ roles}">
                			<option value="${role.getKey() }"><c:out value="${role.getValue() }"></c:out></option>
                		</c:forEach>
					</select>
					<input type="hidden" name="userid" value="${user.id }">
					<button class="button button-style-blue" type="submit" name="show-user-action" value="change-role">Assign</button>
				</form>
			</c:forEach>
			</section>
		</c:if>
		
		<div class="container vertical-flexbox">
			<h3 class="section-title">Role List</h3>
			
			<table class="table-style-list">
                <thead>
                	<tr>
                  		<th scope="col">Name</th>
                    	<th scope="col">Show users</th>
                    	<th scope="col">Edit role</th>
                	</tr>
                </thead>
                <tbody>
                	<c:forEach var="role" items="${ roles}">
                  		<tr>
                    		<th scope="row"><c:out value="${role.getValue() }"></c:out></th>
                    		<td>
                    			<form id="" class="role-list-button" action="RoleManager" method="POST">
                    				<input type="hidden" name="roleid" value="${role.getKey() }">
                    				<button class="button button-style" type="submit" name="role-list-action" value="show-users">show users</button>
                    			</form>
                    		</td>
                    		<td>
                    		
                    		<c:choose>
                    			<c:when test="${imRoles.contains(role.getKey())}">
                    				<div class="role-list-button">
                    					<button class="button button-style-grayout">edit role</button>
                    				</div>
                    			</c:when>
                    			<c:otherwise>
                    				<form id="" class="role-list-button" action="RoleEditing" method="POST">
                    					<input type="hidden" name="roleid" value="${role.getKey() }">
                    					<button class="button button-style-blue" type="submit" name="role-list-action" value="edit-role">edit role</button>
                    				</form>
                    			</c:otherwise>
                    		</c:choose>
                    			
                    		
                    		</td>
                  		</tr>
                  	
                	</c:forEach>
                	<tfoot>
                		<tr>
                			<td colspan="3">
                				<form id="role-editing" class="role-list-button" action="RoleEditing" method="POST">
        							<button class="button button-style-blue" type="submit" name="role-list-action" value="add-role">add role</button>
        						</form>
                			</td>
                		</tr>
                	</tfoot>
                </tbody>
        	</table>
        	
        	
		</div>
		
		
	</main>
	
	<jsp:include page="/WEB-INF/footer.jsp"></jsp:include>
</body>
</html>