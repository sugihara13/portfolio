package wireboutique.dataaccess.database.tables;

public enum OrderDetailsTable implements Table {
	ORDER_ID(1),
	PRODUCT_ID(2),
	PRODUCT_NAME(3),
	PURCHASE_PRICE(4),
	QUANTITY(5),
	TAX_RATE(6);	

	private final int Idx;
	private final String tableName = "order_details";
	
	private OrderDetailsTable(int Idx) {
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
