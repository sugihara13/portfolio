package wireboutique.dataaccess.database.tables;

public enum UsersTable implements Table{
	USER_ID(1),
	PASSWORD(2);
	
	private final int Idx;
	
	private final String tableName = "users";
	
	private UsersTable(int Idx) {
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