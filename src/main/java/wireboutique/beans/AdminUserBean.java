package wireboutique.beans;

import java.io.Serializable;

public class AdminUserBean implements Serializable{
	private static final long serialVersionUID = -482254904512581694L;
	
	//field private
	private String Id;
	private PasswordBean Password;
	private String RoleId;
	private String RoleName;
	
	public AdminUserBean() {
		setId("");
		Password = new PasswordBean();
		setRoleId("");
		setRoleName("");
	}
	
	public AdminUserBean(String id, PasswordBean password,String roleId, String roleName) {
		setId(id);
		setPassword(password);
		setRoleId(roleId);
		setRoleName(roleName);
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

	public String getRoleName() {
		return RoleName;
	}

	public void setRoleName(String roleName) {
		RoleName = roleName;
	}

	public String getRoleId() {
		return RoleId;
	}

	public void setRoleId(String roleId) {
		RoleId = roleId;
	}
}
