package wireboutique.bo.filter.ServiceMgr;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import wireboutique.beans.AdminUserBean;
import wireboutique.bo.PermissionProcessor;

@WebFilter("/ServiceMgr/ProductRegistration")
public class EnterProductsRegistration extends ServiceMgrFilter {

	private static final long serialVersionUID = 1L;
	private String requirePermission = "ADD_PRODUCT";
	
	public EnterProductsRegistration() {
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		requirePermission = "ADD_PRODUCT";
		
		if(request.getParameter("isAddProduct") != null) {
			if(request.getParameter("isAddProduct").equals("false"))
				requirePermission = "MANAGE_PRODUCT_DATA";
		}
		
		super.doFilter(request, response, chain);
	}
	
	@Override
	protected boolean RequirePermission(AdminUserBean user) {
		System.out.println("EnterProductsRegistration RequirePerm: " +requirePermission + " user: "+user.getId());
		
		PermissionProcessor permProc = new PermissionProcessor();
		return permProc.hasPermission(user, requirePermission);
	}
	
	@Override
	protected String errorMsg() {
		return "do not enter ProductsRegistration. check User Permission";
	}
}
