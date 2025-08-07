package wireboutique.dataaccess.image;

import java.awt.image.BufferedImage;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;

public interface ImageDAO {
	BufferedImage getImage(HttpServletRequest request, ServletContext context);
}
