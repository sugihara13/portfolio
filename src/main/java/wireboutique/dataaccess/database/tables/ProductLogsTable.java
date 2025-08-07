package wireboutique.dataaccess.database.tables;

public enum ProductLogsTable implements Table{
	PRODUCT_LOG_ID(1),
	PRODUCT_ID(2),
	UPDATED_AT(3),
	NAME(4),
	CATEGORY_ID(5),
	MANUFACTURER_ID(6),
	LIST_PRICE(7),
	RELEASE_DATE(8),
	CONTENTS(9),
	TAX_CATEGORY_ID(10),
	STOCK(11),
	IS_PUBLIC(12);
	
	private final int Idx;
	private final String tableName ="product_logs";
	
	private ProductLogsTable(int Idx) {
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
