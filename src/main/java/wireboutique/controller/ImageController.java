package wireboutique.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import wireboutique.dataaccess.image.ImageDAO;
import wireboutique.dataaccess.image.ProductImageDAO;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

/**
 * Servlet implementation class ImageController
 * 
 * response image object
 */
@WebServlet("/img")
public class ImageController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ImageController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String type = request.getParameter("type");
		
		ImageDAO iDao  = switch (type) {
			case "product" -> {
				yield new ProductImageDAO();
			}
			case null,default ->{
				yield null;
			}
		};
		
		if(iDao != null) {
			BufferedImage img = iDao.getImage(request, getServletContext());
			
			if(img != null) {	
				OutputStream out = response.getOutputStream();
				response.setContentType("image/jpeg");
				ImageIO.write(img, "jpeg", out);
			}
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	private BufferedImage getImage(HttpServletRequest request) {
		String id = request.getParameter("id");
		String item = request.getParameter("item");
		
		if(id != null && item != null) {
			StringBuilder sb = new StringBuilder("WEB-INF/Assets/products/");
			sb.append(id).append("/contents/").append(item).append(".jpg");
			
			//servletのlocal故 getrealpath 
			//table にWEB-INF/....で記録して接頭辞でlocalか判断とかか
			String path = getServletContext().getRealPath(sb.toString());
		
			try {
				return ImageIO.read(new File(path));
			}
			catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		else return null;
	}
}
