package wireboutique;


import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import wireboutique.beans.ProductBean;
import wireboutique.dataaccess.database.DatabaseDAO;
import wireboutique.dataaccess.database.ProductDAO;
import wireboutique.dataaccess.database.tables.ProductsTable;

import java.io.IOException;
import java.util.List;

/**
 * Servlet implementation class WireBoutique
 */
@WebServlet("/Index")
public class WireBoutique extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public WireBoutique() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		ProductDAO productDAO = new ProductDAO();
		List<ProductBean> products = 
				productDAO.getProducts(ProductsTable.RELEASE_DATE.toString(), 8, DatabaseDAO.DESC,1,true);
		
		request.setAttribute("products",products);
		
		RequestDispatcher dispatcher = 
				request.getRequestDispatcher("/WEB-INF/index.jsp");
		dispatcher.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
