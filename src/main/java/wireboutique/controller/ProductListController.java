package wireboutique.controller;

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
import java.util.ArrayList;
import java.util.List;


/**
 * Servlet implementation class ProductListController
 */
@WebServlet("/ProductList")
public class ProductListController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	//1page あたりのproductの表示数
    private final int displayLimit = 8; 
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ProductListController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int offset = 0;
		Integer page = 1;
		if(request.getParameter("page") != null) {
			try {
				page = Integer.parseUnsignedInt(request.getParameter("page"));
				if(page==0)
					page=1;
			
				offset = (page - 1) * displayLimit;
			}
			catch (NumberFormatException e) {
				e.printStackTrace();
				offset = 0;
			}	
		}
		
		ProductDAO productDAO = new ProductDAO();
		List<ProductBean> products = 
				productDAO.getProducts(ProductsTable.RELEASE_DATE.toString(), displayLimit, DatabaseDAO.DESC, offset, true);
		
		if(products == null)
			products = new ArrayList<ProductBean>();
		
		if(products.size()<displayLimit)
			request.setAttribute("isLastPage",true);
		else request.setAttribute("isLastPage",false);
		
		request.setAttribute("products",products);
		request.setAttribute("pageNum",page);
		
		RequestDispatcher dispatcher = 
				request.getRequestDispatcher("/WEB-INF/ProductList.jsp");
		dispatcher.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		switch (request.getParameter("product-list-action")) {
			case "search"-> {
				ProductDAO productDAO = new ProductDAO();
				List<ProductBean> products = new ArrayList<ProductBean>();
				
				if(request.getParameter("keywords") != null && !request.getParameter("keywords").equals("")) {
					String keyword = request.getParameter("keywords");
					
					//search
					products = productDAO.searchProducts(keyword, displayLimit, DatabaseDAO.DESC, 0, true);
					
					if(products == null)
						products = new ArrayList<ProductBean>();
					
					request.setAttribute("keywords",keyword);
				}
				else {
					products = productDAO.getProducts(ProductsTable.RELEASE_DATE.toString(), displayLimit, DatabaseDAO.DESC, 0, true);
				}
				
				if(products.size()<displayLimit)
					request.setAttribute("isLastPage",true);
				else request.setAttribute("isLastPage",false);
				
				request.setAttribute("products", products);
				request.setAttribute("pageNum",1);
				
				RequestDispatcher dispatcher = 
						request.getRequestDispatcher("/WEB-INF/ProductList.jsp");
				dispatcher.forward(request, response);
			}
			case "next","prev"-> {
				ProductDAO productDAO = new ProductDAO();
				List<ProductBean> products = new ArrayList<ProductBean>();
				
				int offset = 0;
				Integer page = 1;
				if(request.getParameter("page") != null) {
					try {
						page = Integer.parseUnsignedInt(request.getParameter("page"));
						if(page==0)
							page=1;
					
						offset = (page - 1) * displayLimit;
					}
					catch (NumberFormatException e) {
						e.printStackTrace();
						offset = 0;
					}	
				}
				
				if(request.getParameter("keywords") != null && !request.getParameter("keywords").equals("")) {
					String keyword = request.getParameter("keywords");
					products = productDAO.searchProducts(keyword, displayLimit, DatabaseDAO.DESC, offset, true);		
					
					request.setAttribute("keywords",keyword);
				}
				else {
					products = productDAO.getProducts(ProductsTable.RELEASE_DATE.toString(), displayLimit, DatabaseDAO.DESC, offset, true);
				}
				
				if(products.size()<displayLimit)
					request.setAttribute("isLastPage",true);
				else request.setAttribute("isLastPage",false);
				
				request.setAttribute("products", products);
				request.setAttribute("pageNum",page);
				
				RequestDispatcher dispatcher = 
						request.getRequestDispatcher("/WEB-INF/ProductList.jsp");
				dispatcher.forward(request, response);
			}
			case null,default->{
			}
		}
	}

}
