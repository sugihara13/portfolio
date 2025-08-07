package wireboutique.bo.listener;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSessionActivationListener;
import jakarta.servlet.http.HttpSessionAttributeListener;
import jakarta.servlet.http.HttpSessionBindingEvent;
import jakarta.servlet.http.HttpSessionBindingListener;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import wireboutique.beans.ProductContents;

/**
 * Application Lifecycle Listener implementation class ProductEditingListener
 *
 */
@WebListener
public class ProductEditingListener implements HttpSessionListener, HttpSessionAttributeListener, HttpSessionActivationListener, HttpSessionBindingListener {

    /**
     * Default constructor. 
     */
    public ProductEditingListener() {
        // TODO Auto-generated constructor stub
    }

	/**
     * @see HttpSessionAttributeListener#attributeReplaced(HttpSessionBindingEvent)
     */
    public void attributeReplaced(HttpSessionBindingEvent se)  { 
         // TODO Auto-generated method stub
    }

	/**
     * @see HttpSessionActivationListener#sessionDidActivate(HttpSessionEvent)
     */
    public void sessionDidActivate(HttpSessionEvent se)  { 
         // TODO Auto-generated method stub
    }

	/**
     * @see HttpSessionListener#sessionCreated(HttpSessionEvent)
     */
    public void sessionCreated(HttpSessionEvent se)  { 
         // TODO Auto-generated method stub
    }

	/**
     * @see HttpSessionListener#sessionDestroyed(HttpSessionEvent)
     */
    public void sessionDestroyed(HttpSessionEvent se)  {
    	String uploadDirectry = (String)se.getSession().getAttribute(ProductContents.UploadDirectryAttrName);
    	if(uploadDirectry != null) {
	    	Path dir = Paths.get(uploadDirectry);
	    	
	    	if(Files.exists(dir)){
	    		System.out.println("ProductEditing deleteUploadFiles target directry: "+dir);
	
	    		DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>() {
	    			public boolean accept(Path file) throws IOException {
	    		        //return (Files.size(file) > 8192L);
	    				return true;
	    		    }
	    		};
	
	    		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, filter)) {
	    	    	for(Path file: stream) {
	    	    		System.out.println("ProductEditingListener deleteUploadFiles target file: "+file);
	    	    		Files.delete(file);
	    	    	}
	    	    	Files.delete(dir);
	    	    }
	    	    catch (IOException e) {
	    	    	e.printStackTrace();
	    		}
	    	}
    	}
    }

	/**
     * @see HttpSessionBindingListener#valueBound(HttpSessionBindingEvent)
     */
    public void valueBound(HttpSessionBindingEvent event)  { 
         // TODO Auto-generated method stub
    }

	/**
     * @see HttpSessionAttributeListener#attributeRemoved(HttpSessionBindingEvent)
     */
    public void attributeRemoved(HttpSessionBindingEvent se)  { 
         // TODO Auto-generated method stub
    }

	/**
     * @see HttpSessionActivationListener#sessionWillPassivate(HttpSessionEvent)
     */
    public void sessionWillPassivate(HttpSessionEvent se)  { 
         // TODO Auto-generated method stub
    }

	/**
     * @see HttpSessionBindingListener#valueUnbound(HttpSessionBindingEvent)
     */
    public void valueUnbound(HttpSessionBindingEvent event)  { 
         // TODO Auto-generated method stub
    }

	/**
     * @see HttpSessionAttributeListener#attributeAdded(HttpSessionBindingEvent)
     */
    public void attributeAdded(HttpSessionBindingEvent se)  { 
         // TODO Auto-generated method stub
    }
	
}
