package wireboutique.controller.serviceMgr;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import wireboutique.beans.AdminUserBean;
import wireboutique.dataaccess.database.AdminUserDAO;
import wireboutique.dataaccess.database.tables.AdminUserPermissionsTable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Servlet implementation class RoleEditing
 */
@WebServlet("/ServiceMgr/RoleEditing")
public class RoleEditingController extends HttpServlet {
	private static final long serialVersionUID = 1L;
     
	private final Pattern roleNamePattern= Pattern.compile("^[a-zA-Z]{4,32}");
	
	public enum ImmutableRole {
		None("0","Name"),
		Administrator("1","Administrator");
		
		private final String Id;
		private final String Name;
		
		private ImmutableRole(String id,String name){
			this.Id = id;
			this.Name = name;
		}

		public String getId() {
			return Id;
		}

		public String getName() {
			return Name;
		}
	}
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RoleEditingController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.sendRedirect("/ServiceMgr/RoleManager");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//dispatch roleEditingPage
		if(request.getParameter("role-list-action") != null) {
			RoleEditPageDispatcher(request.getParameter("role-list-action"), request, response).forward(request, response);
		}
		
		//editing form action
		switch(request.getParameter("role-editing-action")){
			case "confirm" -> {
				RequestDispatcher dispatcher = null;
				
				//input permissions	LinkedHashMapにして順序を保持してフロントでもKey表示できるようにする
				LinkedHashMap<String,Boolean> inPermissions = new LinkedHashMap<String, Boolean>();
				
				for(AdminUserPermissionsTable perm:AdminUserPermissionsTable.values()) {
					if(perm != AdminUserPermissionsTable.ROLE_ID) {
						if(request.getParameter(perm.column()).equals("true"))
							inPermissions.put(perm.column(), true);
					
						else if(request.getParameter(perm.column()).equals("false"))
							inPermissions.put(perm.column(), false);
					}
				}
				
				//input role name
				String inRoleName =	request.getParameter("role-name");
				boolean isAddRole=false,isEditRole=false;
				
				//validation--------------------------------------------------------
				
				//role name isValid
				if(roleNamePattern.matcher(inRoleName).matches()) {
					//count input permission
					if(inPermissions.size() == AdminUserPermissionsTable.values().length-1) {
						AdminUserDAO auDao = new AdminUserDAO();
						
						//RoleNameからRoleIdを取得 できなければ "" が返る
						String inRoleNametoId = auDao.getRoleId(inRoleName);
						
						
						//isAddRole
						if(request.getParameter("add-role").equals("true")) {
							//
							if(inRoleNametoId.equals("")) {
								//AddRole flag
								isAddRole	=	true;
							}
							else request.setAttribute("msg", "role name" + inRoleName + " alredy regist");
						}
						else if(request.getParameter("add-role").equals("false")) {
							String roleId = request.getParameter("roleid");
							
							//isValidRoleId
							if(roleId != null) {
								boolean isRoleMutable = true;
								for(var imRole :ImmutableRole.values()) {
									if(imRole.getId().equals(roleId)) {
										isRoleMutable	=	false;
										request.setAttribute("msg", "could not change "+inRoleName+" role");
										break;
									}
								}
								
								if(isRoleMutable) {
									if(request.getParameter("roleid").equals(inRoleNametoId) || inRoleNametoId == "") {
										//EditRole flag
										isEditRole	=	true;
									}
									else request.setAttribute("msg", "role name" + inRoleName + " alredy regist");
								}
							}
							else request.setAttribute("msg", "RoleID invalid");
						}
						else request.setAttribute("msg", "add-role parameter invalid");
					}
					else request.setAttribute("msg", "number of Parameters != permission");
				}
				else request.setAttribute("msg", "role name require a~z , A~Z / length 4~32");
				//-------------------------------------------------------------------------
				
				ArrayList<String> differenceMsg	= new ArrayList<String>();
				//ロール追加時
				if(isAddRole) {
					request.setAttribute("isAddRole", true);
					
					differenceMsg.add("add new role");

					request.setAttribute("roleName", inRoleName);
					request.setAttribute("rolePermissions", inPermissions);
					
					request.setAttribute("difference", differenceMsg);

					
					dispatcher = request.getRequestDispatcher("/WEB-INF/ServiceMgr/RoleEditingConfirmation.jsp");
				}
				//ロール編集時
				else if(isEditRole) {
					request.setAttribute("isAddRole", false);
					
					String roleId = request.getParameter("roleid");
					
					AdminUserDAO auDao = new AdminUserDAO();
					HashMap<String,Boolean> rolePermissions =	auDao.getPermissions(roleId);
						
					String roleName = auDao.getRoleName(roleId);
					if(!roleName.equals(inRoleName)) {
						differenceMsg.add("role name "+roleName+" -> "+inRoleName);
					}
					
					for(Map.Entry<String, Boolean> permission:rolePermissions.entrySet()) {
						if(permission.getValue() != inPermissions.get(permission.getKey())) {
							differenceMsg.add(permission.getKey()+" "+permission.getValue()+" -> "+inPermissions.get(permission.getKey()));
						}
					}
					
					request.setAttribute("roleId", request.getParameter("roleid"));
					request.setAttribute("roleName", inRoleName);
					request.setAttribute("rolePermissions", inPermissions);
					
					request.setAttribute("difference", differenceMsg);
						
					dispatcher = request.getRequestDispatcher("/WEB-INF/ServiceMgr/RoleEditingConfirmation.jsp");
				
				}
				else {
					if(request.getParameter("add-role").equals("true"))
						dispatcher = RoleEditPageDispatcher("add-role", request, response);
					
					if(request.getParameter("add-role").equals("false"))
						dispatcher = RoleEditPageDispatcher("edit-role", request, response);
				}
				
				dispatcher.forward(request, response);
			}
			//編集内容登録処理
			case "apply" -> {
				
				AdminUserDAO auDao = new AdminUserDAO();
				
				//get tokens
				
				//get role data
				String roleId = request.getParameter("roleid");
				String inRoleName = request.getParameter("role-name");
				boolean isAddRole=false,isEditRole=false;
				
				//get input permissions
				HashMap<String,Boolean> inPermissions = new HashMap<String, Boolean>();
				
				for(AdminUserPermissionsTable perm:AdminUserPermissionsTable.values()) {
					if(perm != AdminUserPermissionsTable.ROLE_ID) {
						if(request.getParameter(perm.column()).equals("true"))
							inPermissions.put(perm.column(), true);
					
						else if(request.getParameter(perm.column()).equals("false"))
							inPermissions.put(perm.column(), false);
					}
				}
				
				//validation
				if(roleNamePattern.matcher(inRoleName).matches()) {
					//count input permission
					if(inPermissions.size() == AdminUserPermissionsTable.values().length-1) {
						//RoleNameからRoleIdを取得 できなければ "" が返る
						String inRoleNametoId = auDao.getRoleId(inRoleName);
						//isAddRole
						if(request.getParameter("add-role").equals("true")) {
							//
							if(inRoleNametoId == "") {
								//AddRole flag
								isAddRole	=	true;
							}
							else request.setAttribute("msg", "role name" + inRoleName + " alredy regist");
						}
						else if(request.getParameter("add-role").equals("false")) {
							//isValidRoleId
							if(roleId != null) {
								boolean isRoleMutable = true;
								for(var imRole :ImmutableRole.values()) {
									if(imRole.getId().equals(roleId)) {
										isRoleMutable	=	false;
										break;
									}
								}
								
								if(isRoleMutable) {
									if(request.getParameter("roleid").equals(inRoleNametoId) || inRoleNametoId.equals("")) {
										//EditRole flag
										isEditRole	=	true;
									}
								}
							}
						}
					}
				}
				
				//set role
				StringBuilder log = new StringBuilder("Role Editing");
				
				if(isAddRole) {
					if(auDao.setRole(inRoleName, inPermissions) == 2) {
						HttpSession session = request.getSession();
						AdminUserBean user = (AdminUserBean)session.getAttribute("adminUser");
						
						log.append("addRole success:");
						log.append("role id ").append(roleId)
							.append(" name ").append(inRoleName)
							.append(" user :").append(user.getId())
							.append(" user role ").append(user.getRoleName());
						
						System.out.println(log.toString());
						this.getServletContext().log(log.toString());
					}
					
					response.sendRedirect(request.getContextPath()+"/ServiceMgr/RoleManager");
				}
				else if(isEditRole) {
				    int	result=0;
					result	+=	auDao.updateRoleName(roleId, inRoleName);
					result	+=	auDao.updatePermission(roleId, inPermissions);
					
					if(result == 2) {
						HttpSession session = request.getSession();
						AdminUserBean user = (AdminUserBean)session.getAttribute("adminUser");
						
						log.append("addRole success:");
						log.append("role id ").append(roleId)
							.append(" name ").append(inRoleName)
							.append(" user :").append(user.getId())
							.append(" user role ").append(user.getRoleName());
						
						System.out.println(log.toString());
						this.getServletContext().log(log.toString());
					}
					
					response.sendRedirect(request.getContextPath()+"/ServiceMgr/RoleManager");
				}
				else {
					request.setAttribute("errorMsg", "could not apply changes. please try again.");
					
					request.getRequestDispatcher("/WEB-INF/ServiceMgr/ServiceMgrErrorPage.jsp")
						.forward(request, response);
				}
			}
			
			case null,default->{
				
			}
		}
	}
	
	private RequestDispatcher RoleEditPageDispatcher(String mode, HttpServletRequest request, HttpServletResponse response) {
		RequestDispatcher dispatcher = null;
		
		switch (mode){
			//ロール編集用ページへ isAddRole をfalseにしてディスパッチ. ロールを新規で追加する場合にtrue
			case "edit-role" -> {
				String roleId = request.getParameter("roleid");
			
				AdminUserDAO auDao = new AdminUserDAO();
			
				String roleName = auDao.getRoleName(roleId);
			
				if(roleName instanceof String) {
					Map<String, Boolean> rolePermissions = auDao.getPermissions(roleId);
					ArrayList<String> permissionList = new ArrayList<String>();
				
					//permission のリストをテーブル定義から取得 (定義リストを別で作ったほうがいいとは思う)
					for(AdminUserPermissionsTable perm:AdminUserPermissionsTable.values()) {
						if(perm != AdminUserPermissionsTable.ROLE_ID)
							permissionList.add(perm.column());
					}
				
					request.setAttribute("roleId", roleId);
					request.setAttribute("roleName", roleName);
					request.setAttribute("rolePermissions", rolePermissions);
				
					request.setAttribute("permNameList", permissionList);
				
					request.setAttribute("isAddRole", false);
				
					dispatcher = request.getRequestDispatcher("/WEB-INF/ServiceMgr/RoleEditing.jsp");
				}
			}
			//ロール追加もロール編集と同じページへ isAddRole をtrueにしてディスパッチ.
			case "add-role" -> {
				ArrayList<String> permissionList = new ArrayList<String>();
			
				for(AdminUserPermissionsTable perm:AdminUserPermissionsTable.values()) {
					if(perm != AdminUserPermissionsTable.ROLE_ID)
						permissionList.add(perm.column());
				}
			
				request.setAttribute("permNameList", permissionList);
			
				request.setAttribute("isAddRole", true);

				dispatcher = request.getRequestDispatcher("/WEB-INF/ServiceMgr/RoleEditing.jsp");
			}
	
			case null,default->{
				request.setAttribute("errorMsg", "faild action");
				dispatcher = request.getRequestDispatcher("/WEB-INF/ServiceMgr/ServiceMgrErrorPage.jsp");
			}
		}
		
		return dispatcher;
	}

}
