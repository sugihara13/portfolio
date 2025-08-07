package wireboutique.beans;

import java.math.BigDecimal;

public class OrderDetailBean {
	final private String OrderId;
	final private String ProductId;
	final private String ProductName;
	final private BigDecimal PurchasePrice;
	final private int Quantity;
	final private BigDecimal TaxRate;
	
	public OrderDetailBean() {
		this.OrderId = "default";
		this.ProductId = "none";
		this.ProductName = "ddd";
		this.PurchasePrice = null;
		this.Quantity = 0;
		this.TaxRate = null;
	}
	
	public OrderDetailBean(String orderId, String productId, String productName, BigDecimal purchasePrice, int quantity, BigDecimal taxRate) {
		this.OrderId = orderId;
		this.ProductId = productId;
		this.ProductName = productName;
		this.PurchasePrice = purchasePrice;
		this.Quantity = quantity;
		this.TaxRate = taxRate;
	}
	
	public OrderDetailBean(OrderBean order, ProductBean product, BigDecimal purchasePrice, int quantity){
			OrderId = order.getOrderId();
			
			ProductId = product.getId();
			ProductName = product.getName();

			PurchasePrice = purchasePrice;
			TaxRate = product.getTaxRate();
			
			Quantity = quantity;
	}
	
	@Override
	public String toString() {
		StringBuilder sb =new StringBuilder();
		sb.append(getOrderId()+" ").append(getProductId()+" ").append(getProductName()+" ")
					.append(getPurchasePrice()+" ").append(getQuantity()+" ")
					.append(getTaxRate());
		return sb.toString();
	}
	
	public String getOrderId() {
		return OrderId;
	}

	public String getProductId() {
		return ProductId;
	}

	public String getProductName() {
		return ProductName;
	}

	public BigDecimal getPurchasePrice() {
		return PurchasePrice;
	}

	public int getQuantity() {
		return Quantity;
	}

	public BigDecimal getTaxRate() {
		return TaxRate;
	}
}
