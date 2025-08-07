package wireboutique.bo.filter.ServiceMgr;


import jakarta.servlet.annotation.WebFilter;
import wireboutique.beans.AdminUserBean;
import wireboutique.bo.PermissionProcessor;


/**
 * Servlet Filter implementation class EnterServiceMgrPage
 * このクラスを継承してフィルタを作成してもいいかも
 */
@WebFilter({"/ServiceMgr/RoleManager","/ServiceMgr/RoleEditing"})
public class EnterRoleManager extends ServiceMgrFilter {
       
    private static final long serialVersionUID = -3607696767144023213L;

	private final String requirePermission = "EDIT_ADMIN_USER_PERMISSIONS";

	
    public EnterRoleManager() {
        super();
    }

	@Override
	protected boolean RequirePermission(AdminUserBean user) {
		System.out.println("EnterRoleManager RequirePerm: " +requirePermission + " user: "+user.getId());
		
		PermissionProcessor permProc = new PermissionProcessor();
		return permProc.hasPermission(user, requirePermission);
	}
	
	@Override
	protected String errorMsg() {
		return "do not enter RoleManager. check User Permission";
	}
}