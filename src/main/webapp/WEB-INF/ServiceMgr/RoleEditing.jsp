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
			<p><c:out value="${msg }"></c:out></p>
		
			<div class="form-wrapper vertical-flexbox">
    			<form id="role-editing-form" class="" action="RoleEditing" method="POST">
    			<c:choose>
    					<c:when test="${isAddRole == true}">
    						<h3 class="form-heading">Add New Role</h3>
    						
    					</c:when>
    					<c:otherwise>
    						<h3 class="form-heading"><c:out value="RoleName: ${roleName }"></c:out></h3>
    						<p><c:out value="RoleID: ${roleId }"></c:out></p>
    					</c:otherwise>
    			</c:choose>
    			<input type="hidden" name="add-role" value="${isAddRole }">
    			
    			<label class="form-item">
					<p>Role Name</p>
					<input id="role-name" type="text" name="role-name" value="${roleName }" required minlength="4" maxlength="32" pattern="^[a-zA-Z]{4,32}">
    			</label>
    			
    			<h3 class="form-heading">Permissions</h3>
    			
    			<div class="table-wrapper">
    			<table class="table-style-list">
    				<!-- capsiion -->
    				<thead>
    				
    				<tr>
      					<th scope="col">Permission</th>
      					<th scope="col">current</th>
     					<th scope="col">true</th>
     					<th scope="col">false</th>
    				</tr>
    				</thead>
    				<tbody>
    				
					<c:choose>
    					<c:when test="${isAddRole == true}">
    						<c:forEach var="permName" items="${permNameList }">
    							<tr>
	    							<th scope="row">
										<label class="">
											<c:out value="${permName }"></c:out>
										</label>
									</th>
									<td>
										<p><c:out value="--"></c:out></p>
									</td>
									<td>
										<input id="" type="radio" name="${permName }" value="true">
									</td>
									<td>
										<input id="" type="radio" name="${permName }" value="false" checked required>
	                				</td>
                				</tr>
                			</c:forEach>
    					</c:when>
    					
    					<c:otherwise>
							<c:forEach var="permName" items="${permNameList }">
								<tr>
									<th scope="row">
										<label>
											<c:out value="${permName } ${rolepermissions.getKey() }"></c:out>
										</label>
									</th>
									<td>
										<p><c:out value="${rolePermissions.get(permName) }"></c:out></p>
									</td>
									
									<c:if test="${rolePermissions.get(permName) == true}">
									<td>
										<input id="" type="radio" name="${permName }" value="true" checked>
									</td>
									<td>
										<input id="" type="radio" name="${permName }" value="false" required>
                					</td>
                					</c:if>
                					
                					<c:if test="${rolePermissions.get(permName) == false}">
                					<td>
										<input id="" type="radio" name="${permName }" value="true" >
									</td>
									<td>
										<input id="" type="radio" name="${permName }" value="false" checked required>
                					</td>
                					</c:if>
                				</tr>	
                			</c:forEach>
                			<input type="hidden" name="roleid" value="${roleId }">
    					</c:otherwise>
    					
    				</c:choose>
    				
    				</tbody>
					</table>
					</div>
					<button class="form-confirm-button button button-style-blue" type="submit" name="role-editing-action" value="confirm">Confirm</button>
				</form>
			</div>
			
		</div>
	</main>
	
	<jsp:include page="/WEB-INF/footer.jsp"></jsp:include>
</body>
</html>