package wireboutique.beans;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordBean {
	private String Identifier;
	private String Salt;
	private String PasswordHash;
	
	public PasswordBean() {
		Identifier = "Identifier";
		Salt = "Salt";
		PasswordHash = "Digest";
	}
	
	public PasswordBean(String key) {
		StringBuilder pass = new StringBuilder(key);
		
		//create salt---------------------------
		SecureRandom random = new SecureRandom();
		byte bSalt[] = new byte[32];
		random.nextBytes(bSalt);
		Salt = Base64.getEncoder().withoutPadding().encodeToString(bSalt);
		
		pass.append(Salt);
		//--------------------------------------
		
		//building hash-------------------------
		String digest = null;
		Identifier = "SHA-256";//shchme では"5"っぽい(passlib docより) がjavaのenum hashalgorithmと同じにしておく
		
		try {
			//HashAlgorithmはとりあえずSHA-256にしておく springにはbcrypt等ある様子
			MessageDigest md =	MessageDigest.getInstance("SHA-256");
					
			byte[] hash = md.digest(pass.toString().getBytes());

			//BASE64 encoding-------------------------
			digest = Base64.getEncoder().withoutPadding().encodeToString(hash);
			//----------------------------------------
					
			System.out.println("encoded hash:"+digest);
		} catch (NoSuchAlgorithmException e) {
			System.out.println("HashAlgorithm invalid");
			e.printStackTrace();
		}
		finally {
			PasswordHash=digest;
		}
		//-----------------------------------------
	}
	
	//saltを外から与える場合に
	public PasswordBean(String key ,String salt) {
		StringBuilder pass = new StringBuilder(key);
		Salt = salt;
		
		pass.append(Salt);
		
		//building hash-------------------------
		String digest = null;
		Identifier = "SHA-256";//shchme では"5"っぽい(passlib docより) がjavaのenum hashalgorithmと同じにしておく
		
		try {
			//HashAlgorithmはとりあえずSHA-256にしておく springにはbcrypt等ある様子
			MessageDigest md =	MessageDigest.getInstance("SHA-256");
					
			byte[] hash = md.digest(pass.toString().getBytes());

			//BASE64 encoding-------------------------
			digest = Base64.getEncoder().withoutPadding().encodeToString(hash);
			//----------------------------------------
			
		} catch (NoSuchAlgorithmException e) {
			System.out.println("HashAlgorithm invalid");
			e.printStackTrace();
		}
		finally {
			PasswordHash=digest;
		}
		//-----------------------------------------
	}
	
	//parse string mcf
	public boolean parse(String mcf) {
		if(!mcf.startsWith("$"))
			return false;
		
		int hashIdx = mcf.lastIndexOf("$");
			if(hashIdx <= 0)
				return false;
		int saltIdx = mcf.lastIndexOf("$",hashIdx-1);
			if (saltIdx <= 0)
				return false;
		//bcrypt等で使われるパラメータは使わないので検出されれば無効なformatとして扱う
		int paramIdx = mcf.lastIndexOf("$",saltIdx-1);
			if (paramIdx > 0)
				return false;
		
		Identifier = mcf.substring(1,saltIdx);
		Salt = mcf.substring(saltIdx+1,hashIdx);
		PasswordHash = mcf.substring(hashIdx+1);
		
		return true;
	}
	
	@Override
	public String toString() {
		//$identifier$salt$digest
		return "$"+Identifier+"$"+Salt+"$"+PasswordHash;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null || obj instanceof PasswordBean == false)
			return false;
		
		return this.toString().equals(obj.toString());
	}
	
	public String getIdentifier() {
		return Identifier;
	}

	public String getSalt() {
		return Salt;
	}
	
	public String getPasswordHash() {
		return PasswordHash;
	}
}
