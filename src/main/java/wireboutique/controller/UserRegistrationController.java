package wireboutique.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import wireboutique.beans.PasswordBean;
import wireboutique.beans.UserBean;
import wireboutique.dataaccess.database.UserDAO;

import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Servlet implementation class UserRegiistration
 */
@WebServlet("/UserRegistration")
public class UserRegistrationController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private final Pattern idPattern= Pattern.compile("^[\\w]{8,32}");
	private final Pattern passPattern= Pattern.compile("^[\\w\\!-\\/:-@\\[-`\\{-~]{8,32}");
	       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserRegistrationController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher dispatcher = 
				request.getRequestDispatcher("/WEB-INF/UserRegistration.jsp");
		dispatcher.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String	inputId =	request.getParameter("userid");
		PasswordBean	password = new PasswordBean(request.getParameter("password"));	
		boolean inputIsValid=false;
		
		//validation-------------------
		if(idPattern.matcher(inputId).matches()) {
			if(passPattern.matcher(request.getParameter("password")).matches())
			{
				inputIsValid = true;
			}
			else {
				System.out.println("invalid pass");
			}
		}
		else {
			System.out.println("invalid input");
		}
		//-----------------------------
		
		int resultcount=0;
		if(inputIsValid) {
			UserDAO userDAO = new UserDAO();
			
			//同一のuserIdが存在しないことを確認して登録
			if(!userDAO.getUser(inputId).getId().equals(inputId)){
				//create user-------------------
				UserBean user = new UserBean(inputId,password);
				//------------------------------
			
				//Registration------------
				resultcount = userDAO.setUser(user);

				System.out.println("user regitration resultcount "+resultcount+"/ ID:"+user.getId()+" Password:"+user.getPassword());
				//------------------------
			}
			else {
				System.out.println("user regitration faild: Input userID is already registered");
				request.setAttribute("errorMsg", "user regitration faild: Input userID is already registered");
			}		
		}
		
		if(resultcount>0) {
			//登録成功時の処理

			response.sendRedirect("UserAuthentication");
		}
		else {
			//登録失敗時は登録ページに再訪させる.
			doGet(request, response);
		}
	}

}
