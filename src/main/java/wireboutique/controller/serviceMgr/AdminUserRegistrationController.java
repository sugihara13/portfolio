package wireboutique.controller.serviceMgr;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import wireboutique.beans.AdminUserBean;
import wireboutique.beans.PasswordBean;
import wireboutique.dataaccess.database.AdminUserDAO;

import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Servlet implementation class AdminUserRegistration
 */
@WebServlet("/AdminUserRegistration")
public class AdminUserRegistrationController extends HttpServlet {
	private static final long serialVersionUID = 1L;
      
	private final Pattern idPattern= Pattern.compile("^[\\w]{8,32}");
	private final Pattern passPattern= Pattern.compile("^[\\w\\!-\\/:-@\\[-`\\{-~]{8,32}");
	
	//登録直後の初期ロール名
	private final String initRoleName = "None";
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AdminUserRegistrationController() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher dispatcher = 
				request.getRequestDispatcher("/WEB-INF/ServiceMgr/AdminUserRegistration.jsp");
		dispatcher.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String	inputId =	request.getParameter("userid");
		PasswordBean	password = new PasswordBean(request.getParameter("password"));
		//String	role =	request.getParameter("role");
		
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
			AdminUserDAO auDao = new AdminUserDAO();
			
			//同一のuserIdが存在しないことを確認して登録
			
			if(!auDao.getAdminUser(inputId).getId().equals(inputId)){
				//登録直後に割り当てる初期ロールのIdを取得
				String roleId=auDao.getRoleId(initRoleName);
				
				if(roleId != null){
					//create user-------------------
					AdminUserBean user = new AdminUserBean(inputId,password,roleId,initRoleName);
					//------------------------------
			
					//Registration------------
					resultcount = auDao.setAdminUser(user);

					System.out.println("adminUser regitration resultcount "+resultcount+"/ ID:"+user.getId()+" Password:"+user.getPassword());
					//------------------------
				}
				else {
					System.out.println("adminUser regitration faild: initRoleId = null");
					request.setAttribute("errorMsg", "adminUser regitration faild: can't initialized the role ");
				}
			}
			else {
				System.out.println("adminUser regitration faild: Input userID is already registered");
				request.setAttribute("errorMsg", "adminUser regitration faild: Input userID is already registered");
			}		
		}
		
		if(resultcount>0) {
			//登録成功時の処理

			response.sendRedirect("AdminUserAuthentication");
		}
		else {
			//登録失敗時は登録ページに再訪させる.
			doGet(request, response);
		}
	}

}
