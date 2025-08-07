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
import wireboutique.dataaccess.database.ProductDAO;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Servlet implementation class ProductController
 */
@WebServlet("/Product")
public class ProductController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ProductController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		
		String reqId = request.getParameter("Id");
		// validation----------
		/*
		try{
		int Idnum = Integer.valueOf(reqId);
		if(Idnum<1) {
			RequestDispatcher dispatcher = 
			request.getRequestDispatcher("unknown page.jsp");
			dispatcher.forward(request, response);
		}
		}
		catch(){
			RequestDispatcher dispatcher = 
			request.getRequestDispatcher("unknown page.jsp");
			dispatcher.forward(request, response);
		}
		*/
		//---------------------
		
		ProductDAO productDao = new ProductDAO();
		ProductBean product = productDao.getProduct(reqId, true);
		//System.out.println("ProductController doGet product: "+product);
		if(product == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
		else {
			//フロント向けにproductをカートに追加できるかをrequestパラメータへ登録する----------------
			Boolean isValidAddCart = true;
			
			CartBean cart = (CartBean)request.getSession().getAttribute("cart");
			
			if(cart instanceof CartBean) {
				if(cart.containsKey(product))
					isValidAddCart=false;
			}
			if(!product.getReleaseDate().isBefore(LocalDateTime.now()))
				isValidAddCart=false;
			//----------------------------------------------------------------------
			
			request.setAttribute("isValidAddCart", isValidAddCart);
			
			request.setAttribute("product",product);
			
			RequestDispatcher dispatcher = 
				request.getRequestDispatcher("WEB-INF/product.jsp");
			dispatcher.forward(request, response);
		}
}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String reqId = request.getParameter("Id");
		//addcart
		if(null != request.getParameter("addcart")) {
			
			HttpSession session = request.getSession();
			Object CartAttribute = (CartBean)session.getAttribute("cart");
			CartBean cart = null;
			
			//sessionにcartがなければcartを作成
			//instanceof　は null でもfalse //instanceofで型チェックするよ
			if(CartAttribute instanceof CartBean ) {
				cart = (CartBean)CartAttribute;
			}
			else {
				if(CartAttribute != null) {
					//session　のcart AttributeにCartBeanでない型で値があるなら警告とか例外処理すべきか
				}
				cart = new CartBean();
			}
			
			
			ProductDAO productDao = new ProductDAO();
			ProductBean product = productDao.getProduct(reqId, true);

			if(product==null) {
				System.out.println("cart:addcart faild");
			}else {
				if(product.getReleaseDate().isBefore(LocalDateTime.now())) {
					
					//cart内重複check
					if(!cart.containsKey(product)) {
						//product画面からのカート追加は数量を1にする()
						cart.put(product, 1);
						session.setAttribute("cart", cart);
					}
					else {
						//重複している
					}
				}
			}
		}
		
		doGet(request,response);
	}

}