package wireboutique.dataaccess;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import jakarta.servlet.ServletContext;

public class FileDAO {
	public BufferedReader getFile(String path, ServletContext context) {
		StringBuilder realPath = null;
		if(path.substring(0, 8).equals("WEB-INF/"))
			realPath = new StringBuilder(context.getRealPath(path.toString()));
				
		
		try(BufferedReader br= new BufferedReader(new FileReader(realPath.toString()))) {
			return br;
		}
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}