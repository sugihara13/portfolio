package wireboutique.dataaccess.database.tables;

public enum AdminUserPermissionsTable  implements Table{
	ROLE_ID(1),
	ENTER_SERVICE_MGR(2),
	EDIT_ADMIN_USER_PERMISSIONS(3),
	MANAGE_PRODUCT_DATA(4),
	MANAGE_PRODUCT_RESOURCE(5),
	ADD_PRODUCT(6);
	
	private final int Idx;
	
	private final String tableName = "admin_user_permissions";
	
	private AdminUserPermissionsTable(int Idx) {
		this.Idx=Idx;
	}
	
	@Override
	public int getIdx() {
		return this.Idx;
	}
	
	@Override
	public String column() {
		return this.toString();
	}
	
	@Override
	public String tableName() {
		return tableName;
	}
}
