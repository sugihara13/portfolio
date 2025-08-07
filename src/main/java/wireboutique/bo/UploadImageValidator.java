package wireboutique.bo;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;


//アップロードした画像を検証する. 現状はjpgのみ受けつける
public class UploadImageValidator {
	
	private final Pattern filenamePattern = Pattern.compile("^[^\\.\\/\\\\\\:]{1,200}(\\.jpg|\\.jpeg)$");
	private final Pattern extention = Pattern.compile("(\\.[A-Za-z]{1,4}$)");
	
	private final int MaxWidth = 2048;
	private final int MaxHeight = 2048;
	
	public boolean isValidFileName(String filename) {
		return filenamePattern.matcher(filename).matches();
	}
	
	public String getExtension(String filename) {
		Matcher m = extention.matcher(filename);
		if(m.find())
			return m.group(1);
		else return "";
	}
	
	public boolean isValidImage(InputStream target) {
		String extension = null;
		try(ImageInputStream iis = ImageIO.createImageInputStream( target )){
			Iterator<ImageReader> readers = ImageIO.getImageReaders( iis );
			if( !readers.hasNext() )
			{
				return false;
			}
			
			ImageReader reader = readers.next();
			String[] suffixes = reader.getOriginatingProvider().getFileSuffixes();
			if( suffixes == null || suffixes.length == 0 ) {
				extension = suffixes[ 0 ];
			}
			else {
				extension = reader.getFormatName();
			}
			
			if(!extension.equals("JPEG")) {
				return false;
			}
			
			reader.setInput(iis);
			if(reader.getHeight(0) > MaxHeight || reader.getWidth(0) > MaxWidth ) {
				return false;
			}
			
			
			System.out.println("UploadImageValidator isValidimage");
			System.out.println("extention: "+extension);
			System.out.println("height: "+reader.getHeight(0)+" width: "+reader.getWidth(0));
			
			return true;
		}
		catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}