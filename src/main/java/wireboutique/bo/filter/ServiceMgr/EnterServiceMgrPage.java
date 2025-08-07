package wireboutique.bo.filter.ServiceMgr;


import jakarta.servlet.ServletContext;

import jakarta.servlet.annotation.WebFilter;
import wireboutique.beans.AdminUserBean;
import wireboutique.bo.PermissionProcessor;


/**
 * Servlet Filter implementation class EnterServiceMgrPage
 * このクラスを継承してフィルタを作成してもいいかも
 */
@WebFilter("/ServiceMgr/*")
public class EnterServiceMgrPage extends ServiceMgrFilter {
       
    /**
	 * 
	 */
	private static final long serialVersionUID = 6843893384587026691L;

	private final String requirePermission = "ENTER_SERVICE_MGR";

	
    public EnterServiceMgrPage() {
        super();
    }

	@Override
	protected boolean RequirePermission(AdminUserBean user) {
		System.out.println("EnterServiceMgrPage RequirePerm: " +requirePermission + " user: "+user.getId());
		
		PermissionProcessor permProc = new PermissionProcessor();
		return permProc.hasPermission(user, requirePermission);
	}
	
	@Override
	protected String errorMsg() {
		return "do not enter ServiceManager. check User Permission";
	}
}


