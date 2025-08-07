package wireboutique.controller.serviceMgr;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Servlet implementation class CssController
 */
@WebServlet("/ServiceMgr/css")
public class CssController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CssController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		String filePath =
				switch (request.getParameter("sheet")) {
					case "ServiceMgrStylesheet": {
						yield getServletContext().getRealPath("WEB-INF/ServiceMgr/ServiceMgrStylesheet.css");
					}
					case null ,default:{
						yield "";
					}
				};
		
		if(!filePath.equals("")){
			try(BufferedReader br = new BufferedReader(new FileReader(filePath))){
				ServletOutputStream out = response.getOutputStream();
				
				while(br.ready()) {
					out.println(br.readLine());
				}
			
				response.setContentType("text/css");
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		else {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
