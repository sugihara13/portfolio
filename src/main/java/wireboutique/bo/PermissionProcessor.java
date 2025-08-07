package wireboutique.bo;

import java.util.Map;

import wireboutique.beans.AdminUserBean;
import wireboutique.dataaccess.database.AdminUserDAO;

public class PermissionProcessor {
	
	public PermissionProcessor() {
		
	}
	
	public boolean hasPermission(AdminUserBean user, String permission) {
		//権限リストを取得
		AdminUserDAO auDao = new AdminUserDAO();
		Map<String, Boolean> permissions = auDao.getPermissions(user);
		
		//権限名をキーに権限リストから取得
		Boolean hasPerm = permissions.get(permission);
		
		if(permissions.get(permission) == null) {
			System.out.println("PermissionProcessor hasPermission(): unfind "+permission+" role in auDao");
			return false;
		}
		else {
			return hasPerm;
		}
	}
}
