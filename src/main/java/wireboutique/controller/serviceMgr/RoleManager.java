package wireboutique.controller.serviceMgr;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import wireboutique.beans.AdminUserBean;
import wireboutique.controller.serviceMgr.RoleEditingController.ImmutableRole;
import wireboutique.dataaccess.database.AdminUserDAO;
import wireboutique.dataaccess.database.tables.AdminUserPermissionsTable;
import wireboutique.dataaccess.database.tables.AdminUserRolesTable;

import java.io.IOException;
import java.security.Permission;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Servlet implementation class AdminUserRolesMgr
 */
@WebServlet("/ServiceMgr/RoleManager")
public class RoleManager extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private final Pattern idPattern= Pattern.compile("^[\\w]{8,32}");
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RoleManager() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		AdminUserDAO auDao = new AdminUserDAO();	
		Map<String,String> roles = auDao.getRoles();
		ArrayList<String> imRoles = new ArrayList<String>();
		for(ImmutableRole r:ImmutableRole.values())
			imRoles.add(r.getId());
		
		request.setAttribute("imRoles", imRoles);
		request.setAttribute("roles", roles);
		
		RequestDispatcher dispatcher = 
					request.getRequestDispatcher("/WEB-INF/ServiceMgr/RoleManager.jsp");
			dispatcher.forward(request, response);
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		switch (request.getParameter("show-user-action")){
			case "show" -> {
				String inUserId = request.getParameter("userid");
				
				//input userIdの検証
				if(idPattern.matcher(inUserId).matches()) {
					AdminUserDAO auDao = new AdminUserDAO();
					AdminUserBean user = auDao.getAdminUser(inUserId);
					if(user.getId().equals(inUserId)) {
						request.setAttribute("showUser", user);
					}
				}
			}
			
			case "change-role" -> {
				String userId = request.getParameter("userid");
				String newRoleId = request.getParameter("role");
				StringBuilder msg = new StringBuilder("change role");
				
				if(idPattern.matcher(userId).matches()) {
					try {
						if(Integer.parseUnsignedInt(newRoleId)>-1) {
							AdminUserDAO auDao = new AdminUserDAO();
							AdminUserBean user =	auDao.getAdminUser(userId);
							
							//Administratorロールのみ1人以下に出来ないようにする
							if(user.getRoleId().equals(ImmutableRole.Administrator.getId())) {
								if(auDao.getAdminUsers(ImmutableRole.Administrator.getId()).size() > 1) {
									user.setRoleId(newRoleId);
									
									if(auDao.updateAdminUser(user) > 0) {
										msg.append(" success.")
											.append(" user: ").append(user.getId())
											.append(" new role: ").append(auDao.getRoleName(newRoleId));	
									}
									else {
										msg.append(" faild.")
											.append(" user: ").append(user.getId())
											.append(" role: ").append(auDao.getRoleName(newRoleId));
									}
								}
								else {
									msg.append(" faild.")
									.append(" user: ").append(user.getId())
									.append(" role: ").append(auDao.getRoleName(newRoleId));
								}
							}
							else { 
								user.setRoleId(newRoleId);
								
								if(auDao.updateAdminUser(user) > 0) {
									msg.append(" success.")
										.append(" user: ").append(user.getId())
										.append(" new role: ").append(auDao.getRoleName(newRoleId));	
								}
								else {
									msg.append(" faild.")
										.append(" user: ").append(user.getId())
										.append(" role: ").append(auDao.getRoleName(newRoleId));
								}
							}
						}
						else {
							msg.append(" faild. role id is invalid");
						}
					}
					catch (NumberFormatException e) {
						e.printStackTrace();
						msg.append(" faild. role id is invalid");
					}
				}
				else {
					msg.append("faild. user id is invalid");
				}
				
				request.setAttribute("msg",  msg.toString());
			}
			
			case null,default->{
				
			}
		}

		switch (request.getParameter("role-list-action")){
			case "show-users" -> {
				String roleId = request.getParameter("roleid");
				AdminUserDAO auDao = new AdminUserDAO();
				
				List<AdminUserBean> users = auDao.getAdminUsers(roleId);
				
				request.setAttribute("users", users);
			}
			case null,default->{
				
			}
		}

		doGet(request, response);
	}

}
