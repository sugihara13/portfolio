package wireboutique.dataaccess.database;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class DatabaseDAO {
	public static final boolean ASC = true;
	public static final boolean DESC = false;
	
	protected String User = "",Pass="";
	protected String Url= "";
	
	static protected Properties DatabaseProperties = null;
	
	public DatabaseDAO() {
		if(DatabaseProperties!=null)
			setDatabaseInfo();
	}
	
	public static void loadProperties(Path path) {
		try (BufferedReader inStream = Files.newBufferedReader(path))
		{
			DatabaseProperties = new Properties();
			DatabaseProperties.load(inStream);
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		catch (IllegalArgumentException  e) {
			e.printStackTrace();
		}
		
		System.out.println("url "+DatabaseProperties.getProperty("url","")+" user "+DatabaseProperties.getProperty("user",""));
	}
	
	protected void setDatabaseInfo() {
		Url = DatabaseProperties.getProperty("url","");
		User = DatabaseProperties.getProperty("user","");
		Pass = DatabaseProperties.getProperty("password","");
	}
}