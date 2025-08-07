package wireboutique.dataaccess.database.tables;

public enum OrdersTable implements Table {
	ORDER_ID(1),
	TOTAL_PRICE(2),
	ORDER_DATE(3),
	PAYMENT_STATE(4),
	PAYMENT_METHODE(5),
	USER_ID(6),
	PURCHASER(7);

	private final int Idx;
	private final String tableName = "orders";
	
	private OrdersTable(int Idx) {
		this.Idx = Idx;
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
