package wireboutique.dataaccess.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import wireboutique.dataaccess.database.ProductDAO;

public class ProductImageDAO implements ImageDAO {

	@Override
	public BufferedImage getImage(HttpServletRequest request, ServletContext context) {
		String id = request.getParameter("id");
		String item = request.getParameter("item");
		
		if(id != null && !id.equals("")
			&& item != null && !item.equals("")) {
			
			ProductDAO dao = new ProductDAO();
			String contentUrl = dao.getContentUrl(id);
			
			if(contentUrl != null) {
				// product.id/contents までのパス
				StringBuilder path = new StringBuilder(contentUrl);
				path.append(item).append(".jpg");
			

				//servletのlocal故 getrealpath 
				//table にWEB-INF/....で記録して接頭辞でlocalか判断
				if(path.substring(0, 8).equals("WEB-INF/"))
					path = new StringBuilder(context.getRealPath(path.toString()));
				
				//System.out.println("ProductImageDAO getImage filepath: "+path);
				
				try {
					return ImageIO.read(new File(path.toString()));
				}
				catch (IOException e) {
					e.printStackTrace();
					
					return null;
				}
			}
			else {
				return null;
			}
		}
		else return null;
	}

}
