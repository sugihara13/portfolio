package wireboutique.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import wireboutique.beans.CartBean;
import wireboutique.beans.ProductBean;
import wireboutique.beans.UserBean;
import wireboutique.bo.CartValidator;

import java.io.IOException;
import java.util.Map;

/**
 * Servlet implementation class CartController
 */
@WebServlet("/Cart")
public class CartController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CartController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		
		if(session.getAttribute("cart") instanceof CartBean) {
			CartBean cart = (CartBean)session.getAttribute("cart");
			CartValidator cv = new CartValidator(cart);
		
			Map<String,String> ivProducts = cv.invalidProducts();
			request.setAttribute("ivProducts", ivProducts);
		}
		
		RequestDispatcher dispatcher = 
				request.getRequestDispatcher("/WEB-INF/Cart.jsp");
		dispatcher.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//remove
		if(null != request.getParameter("remove")) {
			HttpSession session = request.getSession();
			CartBean cart = (CartBean)session.getAttribute("cart");
			
			String reqId = request.getParameter("remove");
			
			/*
			for(int i = cart.size()-1; i > -1; i--) {
				if(cart.get(i).getId().equals(reqId)) {
					System.out.println("cart/remove:"+cart.remove(i));
					break;
				}
			}
			*/
			
			ProductBean removeKey=null;
			for(Map.Entry<ProductBean,Integer> c:cart.entrySet()) {
				if(c.getKey().getId().equals(reqId)) {
					removeKey = c.getKey();
					break;
				}
			}
			if(removeKey!=null)
				System.out.println("cart/remove:"+cart.remove(removeKey));
			
			session.setAttribute("cart", cart);
		}
		
		//clear cart
		if(null != request.getParameter("clear")) {
			System.out.println(request.getParameter("clear cart"));
			
			HttpSession session = request.getSession();
			CartBean cart = (CartBean)session.getAttribute("cart");
			
			if(null != cart) {
				cart.clear();
				session.setAttribute("cart", cart);
			}
		}
		
		doGet(request, response);
	}

}
