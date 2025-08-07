package wireboutique.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import wireboutique.beans.CartBean;
import wireboutique.beans.PasswordBean;
import wireboutique.beans.UserBean;
import wireboutique.dataaccess.database.UserDAO;

import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Servlet implementation class UserAuthenticationController
 */
@WebServlet("/UserAuthentication")
public class UserAuthenticationController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private final Pattern idPattern= Pattern.compile("^[\\w]{8,32}");
	private final Pattern passPattern= Pattern.compile("^[\\w\\!-\\/:-@\\[-`\\{-~]{8,32}");
	
	//1セッションでの認証失敗の許容回数//sessionscopeにsetすべきか
	private final int AuthChallengeLimit = 30;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserAuthenticationController() {
        super();
        // TODO Auto-generated constructor stub
        
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher dispatcher = 
				request.getRequestDispatcher("WEB-INF/UserAuthentication.jsp");
		dispatcher.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		boolean authResult=false;
		String inputId = request.getParameter("userid");
		String inputPassword = request.getParameter("password");
		
		//validation--------------------------
		boolean inputIsValid = false;
		//[]{8.32}	
		if(idPattern.matcher(inputId).matches()) {
			if(passPattern.matcher(inputPassword).matches())
				inputIsValid = true;
		}
		//------------------------------------
		
		//authentication----------------------
		UserBean authUser =null;
		if(inputIsValid) {
			//DBからID照合
			UserDAO userDao =new UserDAO();
			authUser = userDao.getUser(inputId);
			
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
			
			//引継ぐパラメータ
			CartBean cart = (CartBean)session.getAttribute("cart");
			
			session.invalidate();
			session = request.getSession(true);
			
			session.setAttribute("user",authUser);
			
			//引継ぎ
			if(cart instanceof CartBean)
				session.setAttribute("cart",cart);
			
			//とりあえずindexにとばす
			/*RequestDispatcher dispatcher = 
					request.getRequestDispatcher("WireBoutique");
			dispatcher.forward(request, response);
			*/
			response.sendRedirect("Index");
		}
		else {
			//認証失敗時は認証ページに再訪させる.
			//一応回数制限つける.
			ErrorCount++;
			session.setAttribute("AuthError",ErrorCount);
			
			request.setAttribute("errorMsg", "Login faild");
			
			doGet(request, response);
		}
		
		System.out.println("authentication result:"+authResult+"/ ID:"+inputId +"ErrorCount:"+ErrorCount);
	}

}
