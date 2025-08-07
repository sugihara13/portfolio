package wireboutique.controller.serviceMgr;

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
 * Servlet implementation class ServicrMgrProductList
 */
@WebServlet("/ServiceMgr/ProductList")
public class ServicrMgrProductList extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	private final int displayLimit = 4;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ServicrMgrProductList() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ProductDAO productDAO = new ProductDAO();
		List<ProductBean> products = productDAO.getProducts(ProductsTable.RELEASE_DATE.toString(), displayLimit, DatabaseDAO.DESC, 0, false);
		
		if(products.size()<displayLimit)
			request.setAttribute("isLastPage",true);
		else request.setAttribute("isLastPage",false);
		
		request.setAttribute("products", products);
		request.setAttribute("pageNum",1);
		
		RequestDispatcher dispatcher = 
					request.getRequestDispatcher("/WEB-INF/ServiceMgr/ServiceMgrProductList.jsp");
			dispatcher.forward(request, response);
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		switch (request.getParameter("product-list-action")) {
			case "search"-> {
				if(request.getParameter("keywords") != null && !request.getParameter("keywords").equals("")) {
					String keyword = request.getParameter("keywords");
					ProductDAO productDAO = new ProductDAO();
					//search
					
					List<ProductBean> products = productDAO.searchProducts(keyword, displayLimit, DatabaseDAO.DESC, 0, false);
					
					if(products.size()<displayLimit)
						request.setAttribute("isLastPage",true);
					else request.setAttribute("isLastPage",false);
					
					request.setAttribute("products", products);
					request.setAttribute("keywords",keyword);
				}
				else {
					request.setAttribute("products", null);
					request.setAttribute("isLastPage",true);
				}
				
				request.setAttribute("pageNum",1);
				
				RequestDispatcher dispatcher = 
						request.getRequestDispatcher("/WEB-INF/ServiceMgr/ServiceMgrProductList.jsp");
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
					products = productDAO.searchProducts(keyword, displayLimit, DatabaseDAO.DESC, offset, false);		
					
					request.setAttribute("keywords",keyword);
				}
				else {
					products = productDAO.getProducts(ProductsTable.RELEASE_DATE.toString(), displayLimit, DatabaseDAO.DESC, offset, false);
				}
				
				if(products.size()<displayLimit)
					request.setAttribute("isLastPage",true);
				else request.setAttribute("isLastPage",false);
				
				request.setAttribute("products", products);
				request.setAttribute("pageNum",page);
				RequestDispatcher dispatcher = 
						request.getRequestDispatcher("/WEB-INF/ServiceMgr/ServiceMgrProductList.jsp");
					dispatcher.forward(request, response);
			}
			case null,default->{
			}
		}
	}

}
