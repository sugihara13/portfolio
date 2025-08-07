package wireboutique.dataaccess.database.tables;

public enum CategoriesTable implements Table{
	CATEGORY_ID(1),
	CATEGORY(2);
	
	private final int Idx;
	private final String tableName ="categories";
	
	private CategoriesTable(int Idx) {
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
