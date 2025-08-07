package wireboutique.dataaccess.database.tables;

public enum TaxCategoriesTable implements Table {
	TAX_CATEGORY_ID(1),
	TAX_NAME(2),
	TAX_RATE(3);
	
	private final int Idx;
	private final String tableName = "tax_categories";
	
	private TaxCategoriesTable(int Idx) {
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
