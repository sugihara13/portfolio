package wireboutique.controller.serviceMgr;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import wireboutique.beans.AdminUserBean;
import wireboutique.beans.PasswordBean;
import wireboutique.dataaccess.database.AdminUserDAO;

import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Servlet implementation class AdminUserAuthentication
 */
@WebServlet("/AdminUserAuthentication")
public class AdminUserAuthenticationController extends HttpServlet {
	private static final long serialVersionUID = 1L;
      
	private final Pattern idPattern= Pattern.compile("^[\\w]{8,32}");
	private final Pattern passPattern= Pattern.compile("^[\\w\\!-\\/:-@\\[-`\\{-~]{8,32}");
	
	//1セッションでの認証失敗の許容回数
	private final int AuthChallengeLimit = 30;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AdminUserAuthenticationController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher dispatcher = 
				request.getRequestDispatcher("/WEB-INF/ServiceMgr/AdminUserAuthentication.jsp");
		dispatcher.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		boolean authResult=false;
		String	inputId =	request.getParameter("userid");
		String	inputPassword = request.getParameter("password");
		
		//validation-------------------------
		boolean inputIsValid = false;
		//[]{8.32}	
		if(idPattern.matcher(inputId).matches()) {
			if(passPattern.matcher(inputPassword).matches())
				inputIsValid = true;
		}
		//-----------------------------------
		
		
		//authentication---------------------
		AdminUserBean authUser =null;
		
		if(inputIsValid) {
			//DBからID照合
			AdminUserDAO auDao =new AdminUserDAO();
			
			authUser = auDao.getAdminUser(inputId);
			
			if(authUser == null)
				authUser = new AdminUserBean();
			
			//Idが取得できていなくてもパスワードをチェック その上でIdチェック
			PasswordBean password = new PasswordBean(inputPassword,authUser.getPassword().getSalt());	
			if(authUser.getPassword().equals(password)) {		
				if(authUser.getId().equals(inputId)) {
					authResult = true;
				}
				else {
					authResult = false;
				}
			}
		}
			
		
		//Limiting challenge-----------------
		//入力に関わらず1セッション中の失敗許容回数を超えていたら失敗にする
		HttpSession session = request.getSession();
		int ErrorCount=0;
		try {
			ErrorCount = (int)session.getAttribute("AuthError");
		}
		catch (NullPointerException n){
			ErrorCount = 0;
		}
	
		if(ErrorCount > AuthChallengeLimit)
			authResult=false;
		//-----------------------------------
		
		//------------------------------------
		
		//dispatch---------------------------
		if(authResult) {
			//認証成功時の処理
			
			session.invalidate();
			session = request.getSession(true);
			
			session.setAttribute("adminUser",authUser);
			
			//ServiceManager トップへリダイレクト
			response.sendRedirect("ServiceMgr");
		}
		else {
			//認証失敗時は認証ページに再訪させる.
			//一応回数制限つける.
			ErrorCount++;
			session.setAttribute("AuthError",ErrorCount);
			
			request.setAttribute("errorMsg", "Login faild");
			
			doGet(request, response);
		}
		
		System.out.println("adminUser authentication result:"+authResult+"/ ID:"+inputId +"ErrorCount:"+ErrorCount);
	}

}
