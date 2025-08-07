package wireboutique.dataaccess.database.tables;

public enum ManufacturersTable implements Table{
	MANUFACTURER_ID(1),
	MANUFACTURER(2);
	
	private final int Idx;
	private final String tableName = "manufacturers";
	
	private ManufacturersTable(int Idx) {
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
