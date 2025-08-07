package wireboutique.dataaccess.database.tables;

public enum AdminUsersTable implements Table{
	ADMIN_USER_ID(1),
	PASSWORD(2),
	ROLE_ID(3);
	
	private final int Idx;
	
	private final String tableName = "admin_users";
	
	private AdminUsersTable(int Idx) {
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
