package wireboutique.dataaccess.database.tables;

public enum AdminUserRolesTable implements Table {
	ROLE_ID(1),
	ROLE_NAME(2);
	
	private final int Idx;
	
	private final String tableName = "admin_user_roles";
	
	private AdminUserRolesTable(int Idx) {
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
