package wireboutique.controller.serviceMgr;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import wireboutique.beans.ProductBean;
import wireboutique.dataaccess.database.ProductDAO;

import java.io.IOException;

/**
 * Servlet implementation class ProductEditingController
 */
@WebServlet("/ServiceMgr/ProductEditing")
public class ProductEditingController extends ProductRegistrationContloller {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ProductEditingController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.sendRedirect("/ServiceMgr/ProductList");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
    @Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		switch (request.getParameter("product-list-action")) {
			case "edit-role"-> {
				if(request.getParameter("product-id") != null){
					String productId = request.getParameter("product-id");
					
					ProductDAO productDAO = new ProductDAO();
					ProductBean product = productDAO.getProduct(productId, false);
					
					if(product != null) {
						setEditMode(request, product);
						super.doGet(request, response);
					}
					else {
						request.setAttribute("errorMsg", "could not get product.");
						
						request.getRequestDispatcher("/WEB-INF/ServiceMgr/ServiceMgrErrorPage.jsp")
							.forward(request, response);
					}
				}
				else {
					request.setAttribute("errorMsg", "could not get product.");
					
					request.getRequestDispatcher("/WEB-INF/ServiceMgr/ServiceMgrErrorPage.jsp")
						.forward(request, response);
				}
			}
			case null,default->{
				request.setAttribute("errorMsg", "Error");
				
				request.getRequestDispatcher("/WEB-INF/ServiceMgr/ServiceMgrErrorPage.jsp")
					.forward(request, response);
			}
		}
	}

}
