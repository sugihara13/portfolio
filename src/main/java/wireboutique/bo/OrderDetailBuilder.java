package wireboutique.bo;

import java.math.BigDecimal;

import wireboutique.beans.OrderBean;
import wireboutique.beans.OrderDetailBean;
import wireboutique.beans.ProductBean;

//orderDetail のパラメータが多くなるならbuilderもありかも(イミュータブル故)
//よほど長くならない限りはOrderDetailのコンストラクタでいい(このbuilderは使わない)
public class OrderDetailBuilder {
	private String OrderId;
	private String ProductId;
	private String ProductName;
	private BigDecimal PurchasePrice;
	private int Quantity;
	private BigDecimal TaxRate;
	
	public OrderDetailBuilder() {
		this.OrderId = "";
		this.ProductId = "";
		this.ProductName = "";
		this.PurchasePrice = null;
		this.Quantity = 0;
		this.TaxRate = null;
	}
	
	public OrderDetailBuilder(OrderBean order, ProductBean product, BigDecimal purchasePrice, int quantity){
		setOrderId(order);
		setProduct(product);
		setPrice(purchasePrice);
		setQuantity(quantity);
	}
	
	public void setOrderId(OrderBean order) {
		OrderId = order.getOrderId();
	}
	
	public void setProduct(ProductBean product) {
		ProductId = product.getId();
		ProductName = product.getName();
		TaxRate = product.getTaxRate();
	}
	
	public void setPrice(BigDecimal purchasePrice) {
		PurchasePrice = purchasePrice;
	}
	
	public void setQuantity(int quantity) {
		Quantity = quantity;
	}
	
	public OrderDetailBean building() {
		return new OrderDetailBean(OrderId, ProductId, ProductName, PurchasePrice, Quantity, TaxRate);
	}
}
