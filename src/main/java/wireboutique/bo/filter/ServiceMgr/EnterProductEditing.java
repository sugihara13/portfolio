package wireboutique.bo.filter.ServiceMgr;

import jakarta.servlet.annotation.WebFilter;
import wireboutique.beans.AdminUserBean;
import wireboutique.bo.PermissionProcessor;

@WebFilter("/ServiceMgr/ProductEditing")
public class EnterProductEditing extends ServiceMgrFilter {

	private static final long serialVersionUID = 1L;
	private final String requirePermission = "MANAGE_PRODUCT_DATA";
	
	public EnterProductEditing() {
	}
	
	@Override
	protected boolean RequirePermission(AdminUserBean user) {
		System.out.println("EnterProductEditing RequirePerm: " +requirePermission + " user: "+user.getId());
		
		PermissionProcessor permProc = new PermissionProcessor();
		return permProc.hasPermission(user, requirePermission);
	}
	
	@Override
	protected String errorMsg() {
		return "do not enter ProductEditing. check User Permission";
	}
}