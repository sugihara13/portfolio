package wireboutique.beans;

import java.io.Serializable;

public class UserBean implements Serializable{
	private static final long serialVersionUID = -8709310426325563344L;

	//field private
		private String Id;
		private PasswordBean Password;
	
	public UserBean() {
		Id = "";
		Password = new PasswordBean();
	}
		
	public UserBean(String id,PasswordBean password) {
		setId(id);
		setPassword(password);
	}
	
	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}
	public PasswordBean getPassword() {
		return Password;
	}

	public void setPassword(PasswordBean password) {
		Password = password;
	}
}
