package wireboutique.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import wireboutique.beans.CartBean;
import wireboutique.beans.OrderBean;
import wireboutique.beans.OrderDetailBean;
import wireboutique.beans.ProductBean;
import wireboutique.beans.UserBean;
import wireboutique.bo.CartValidator;
import wireboutique.bo.Payment.PaymentMethod;
import wireboutique.bo.Payment.PaymentState;
import wireboutique.bo.UlidBuilder;
import wireboutique.dataaccess.database.OrdersDAO;
import wireboutique.dataaccess.database.ProductDAO;

/**
 * Servlet implementation class CheckoutController
 * chckout action (controller)parameter
 * (checkout)checkout -> (payment)payment -> (payment)confirm -> (checkout)order
 * 
 */
@WebServlet("/Checkout")
public class CheckoutController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CheckoutController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//GET できたらerrorかトップページかかな
		RequestDispatcher dispatcher = 
				request.getRequestDispatcher("/WEB-INF/ErrorPage.jsp");
		dispatcher.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {	
		switch (request.getParameter("checkout")) {
			case "checkout" -> {
				HttpSession session = request.getSession();
				
				//ログインしていなければログインページへリダイレクト
				if(session.getAttribute("user") instanceof UserBean) {
					CartBean cart = (CartBean)session.getAttribute("cart");
					
					CartValidator cv = new CartValidator(cart);
					if(cv.isValid()) {
						//enter checkout page
						RequestDispatcher dispatcher = 
								request.getRequestDispatcher("/WEB-INF/Checkout.jsp");
						dispatcher.forward(request, response);
					}
					else {
						/*
						Map<String,String> ivProducts = cv.invalidProducts();
						request.setAttribute("ivProducts", ivProducts);
						*/
						response.sendRedirect("Cart");
					}
				}
				else {	
					response.sendRedirect("UserAuthentication");
				}
			}		
			case "order" -> {
				//order registration
				HttpSession session = request.getSession();
				boolean isValidUser = false,isValidCart = false,isValidPaymentData = false;
				
				//get user
				UserBean user = null;
				String purchaser = null;
				if(session.getAttribute("user") instanceof UserBean) {
					user = (UserBean)session.getAttribute("user");
					
					//get purchaser
					if(session.getAttribute("purchaser") instanceof String) {
						purchaser = (String)session.getAttribute("purchaser");
						isValidUser =true;
					}
				}
				
				//get cart
				CartBean cart = null;
				if(session.getAttribute("cart") instanceof CartBean) {
					cart = (CartBean)session.getAttribute("cart");
					CartValidator cv = new CartValidator(cart);
					if(cv.isValid()) {	
						isValidCart = true;
					}
				}
				
				//get payment
				PaymentState paymentState = null;
				PaymentMethod paymentMethod = null;
				if(session.getAttribute("PaymentState") instanceof PaymentState
						&& session.getAttribute("PaymentMethod") instanceof PaymentMethod) {
					paymentState = (PaymentState)session.getAttribute("PaymentState");
					paymentMethod = (PaymentMethod)session.getAttribute("PaymentMethod");
					isValidPaymentData = true;
				}
				
				if (isValidUser	&&	isValidCart	&&	isValidPaymentData) {
					//building order-------------------------------
					//order
					//id
					UlidBuilder idBuilder = new UlidBuilder();
					String orderId = idBuilder.buildId();
					
					BigDecimal totalPrice = cart.total();
					LocalDateTime orderDate = LocalDateTime.now();
				
					OrderBean order = new OrderBean(orderId,totalPrice,orderDate,paymentState.toString(),paymentMethod.toString(),user.getId(),purchaser);
				
					//datails
					ArrayList <OrderDetailBean> orderDetails = new ArrayList<OrderDetailBean>();
					for(CartBean.Entry<ProductBean,Integer> product :cart.entrySet()) {
						//購入価格 商品価格にかかるバウチャー等のその他ディスカウントあればここで反映する(セール,商品割引はproductBeanから取得する)
						BigDecimal purchasePrice = product.getKey().IncTaxPrice();
					
						orderDetails.add(new OrderDetailBean(order, product.getKey() ,purchasePrice ,product.getValue()));
					}
				
					//---------------------------------------------
				
				
					//order registration-------------------
					OrdersDAO orderDao = new OrdersDAO();
				
					if(orderDao.setOrder(order, orderDetails) < 1) {
						//registration faild
						RequestDispatcher dispatcher = 
								request.getRequestDispatcher("/WEB-INF/Checkout.jsp");
						dispatcher.forward(request, response);
					}
					else {
						//clearcart
						session.removeAttribute("cart");
						
						//RemoveAttribute
						session.removeAttribute("PaymentState");
						session.removeAttribute("PaymentMethod");
						session.removeAttribute("purchaser");
						
						//getOrder
						orderDao.getOrder(orderId);
						orderDao.getOrderDetails(orderId);
						
						//set requestscope
						request.setAttribute("order", order);
						request.setAttribute("orderDetails", orderDetails);
					
						RequestDispatcher dispatcher = 
							request.getRequestDispatcher("/WEB-INF/OrderConfirmed.jsp");
						dispatcher.forward(request, response);
					}
					//-------------------------------------
				
					
				}
				else if(isValidUser	&&	!isValidCart	&&	isValidPaymentData) {
					//cartのみfalseの場合Cartページへ返す
					response.sendRedirect("Cart");
				}
				
				else {
					//remove attribute
					
					//error
					RequestDispatcher dispatcher = 
							request.getRequestDispatcher("/WEB-INF/ErrorPage.jsp");
					dispatcher.forward(request, response);
				}
			}
			
			case null,default -> {
				//error
				RequestDispatcher dispatcher = 
						request.getRequestDispatcher("/WEB-INF/ErrorPage.jsp");
				dispatcher.forward(request, response);
			}
		};
	}
}