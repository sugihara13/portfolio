package wireboutique.bo.filter.ServiceMgr;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import wireboutique.beans.AdminUserBean;
import wireboutique.bo.PermissionProcessor;

import java.io.IOException;

/**
 * Servlet Filter implementation class EnterServiceMgrPage
 * このクラスを継承してフィルタを作成
 */

public abstract class ServiceMgrFilter extends HttpFilter implements Filter {
       
    /**
	 * 
	 */	
	private static final long serialVersionUID = -6279359733541235368L;

	/**
     * @see HttpFilter#HttpFilter()
     */
    public ServiceMgrFilter() {
        super();
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpSession session = ((HttpServletRequest)request).getSession();
		
		AdminUserBean user = null;
		if(session.getAttribute("adminUser") instanceof AdminUserBean){
			user = (AdminUserBean)session.getAttribute("adminUser");
			
			//check permission

			//権限があればページへ遷移
			if(RequirePermission(user)) {
					chain.doFilter(request, response);
			}
			else {
				//loginしていて権限がない場合	//msgつけてServiceMgrのエラーページへ
				request.setAttribute("errorMsg", errorMsg());
				
				RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/ServiceMgr/ServiceMgrErrorPage.jsp");
	            dispatcher.forward(request,response);
			}
		}
		else {
			//loginせずServiceMgrにアクセスされたらErrorpageへ
			RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/ErrorPage.jsp");
            dispatcher.forward(request,response);
		}
		
		
	}
	
	//権限確認する処理 overrideして権限を確認する
	protected abstract boolean RequirePermission(AdminUserBean user);
	
	//loginしている状態で権限がない場合のエラーメッセージ overrideしてカスタマイズ
	protected abstract String errorMsg();

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
