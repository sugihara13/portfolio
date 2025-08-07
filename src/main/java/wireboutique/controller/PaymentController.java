package wireboutique.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import wireboutique.beans.CartBean;
import wireboutique.bo.Payment;
import wireboutique.bo.Payment.*;

import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Servlet implementation class PaymentController
 */
@WebServlet("/Payment")
public class PaymentController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private final Pattern PurchaserPattern= Pattern.compile("^[\\wぁ-んァ-ヶｱ-ﾝﾞﾟ一-龠ー]{8,50}");
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PaymentController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//payment処理へはpostのみで
		//get されたらindexへ(エラーページがあればそっちか)
		response.sendRedirect("WEB-INF/Error.jsp");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		switch (request.getParameter("payment")) {
		
		//dispatch PaymentPage
		case "inputform" -> {
				HttpSession session = request.getSession();
				
				//cart check 商品があれば支払いフォームへ
				if(session.getAttribute("cart") instanceof CartBean) {
					CartBean cart=	(CartBean)session.getAttribute("cart");
					
					if(cart.size() > 0) {
						//ここで商品情報を取得し直してもいいかも (不正な商品チェックと商品情報を最新にするため)
					
						
						//form values 
						request.setAttribute("pmCard", PaymentMethod.CARD);
						request.setAttribute("pmBank", PaymentMethod.BANK);
						
						RequestDispatcher dispatcher = 
								request.getRequestDispatcher("/WEB-INF/PaymentForm.jsp");
							dispatcher.forward(request, response);
					}
					else {
						//error	cart に商品がない
						RequestDispatcher dispatcher = 
								request.getRequestDispatcher("/WEB-INF/ErrorPage.jsp");
						dispatcher.forward(request, response);
					}
				}
				else {
					//error cartが作成されていない
					RequestDispatcher dispatcher = 
							request.getRequestDispatcher("/WEB-INF/ErrorPage.jsp");
					dispatcher.forward(request, response);
				}
		}
		
		//paymentform validation and formdata regist session
		case "confirm" -> {
			boolean inputIsValid = true;
			
			//form validation--------------------
			String purchaser = request.getParameter("purchaser");
			PaymentMethod method = PaymentMethod.toMethod(request.getParameter("payment-method"));
			if(PurchaserPattern.matcher(purchaser).matches()) {
				if(!method.equals(PaymentMethod.UNDEFIND)) {
					inputIsValid = true;
				}		
			}
			//-----------------------------------
			
			if(inputIsValid) {
				//processing payment
				Payment paymentSys = new Payment();
				
				//実際の決済システムを実装しているわけでないので処理はない
				paymentSys.registPayment(request.getParameter("payment-method"));
				//getStateは適当な値が返ってくる(実際は初期値で登録だろうけど)
				PaymentState paymentState = paymentSys.getState();
				
				HttpSession session = request.getSession();
				session.setAttribute("PaymentState", paymentState);
				session.setAttribute("PaymentMethod", method);
				session.setAttribute("purchaser", request.getParameter("purchaser"));
				//dispatch
				RequestDispatcher dispatcher = 
						request.getRequestDispatcher("WEB-INF/PaymentConfirmation.jsp");
				dispatcher.forward(request, response);
			}
			else {
				//form input invalid
				//set parameter------------
					//フォーム入力の補助メッセージ (***が不正ですとか)をセット
				//-------------------------
				RequestDispatcher dispatcher = 
						request.getRequestDispatcher("/WEB-INF/PaymentForm.jsp");
					dispatcher.forward(request, response);
			}
		}
		
		case null,default -> {
			//Error
			response.sendRedirect("WEB-INF/ErrorPage.jsp");
		}
		}
	}
}
