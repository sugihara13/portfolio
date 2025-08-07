package wireboutique.beans;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class OrderBean {
	final private String OrderId;
	final private BigDecimal TotalPrice;
	final private LocalDateTime OrderDate;
	final private String PaymentState; //暫定String Paymentstate定義型
	final private String PaymentMethod;//暫定string　paymentMethod型
	final private String UserId;
	final private String Purchaser;
	
	//フィールドでdetailをもつ必要はないか
	private ArrayList<OrderDetailBean> Details=null;
	
	public OrderBean() {
		this.OrderId = "default";
		this.TotalPrice = null;
		this.OrderDate = null;
		this.PaymentState = "";
		this.PaymentMethod = "none";
		this.UserId = "none";
		this.Purchaser = "none";
		
	}
	
	public OrderBean(String orderId, BigDecimal totalPrice, LocalDateTime orderdate, String paymentState, String paymentMethod, String userId, String purchaser) {
		this.OrderId = orderId;
		this.TotalPrice = totalPrice;
		this.OrderDate = orderdate;
		this.PaymentState = paymentState;
		this.PaymentMethod = paymentMethod;
		this.UserId = userId;
		this.Purchaser = purchaser;
	}
	
	public boolean addDetail(OrderDetailBean detail) {
		
		Details.add(detail);
		
		return false;
	}
	
	public ArrayList<OrderDetailBean> getDetails() {
		return Details;
	}
	
	public boolean detailExsist() {
		return Details != null;
	}
	
	@Override
	public String toString() {
		StringBuilder sb =new StringBuilder();
		sb.append(getOrderId()+" ").append(getTotalPrice()+" ").append(getOrderDate()+" ")
					.append(getPaymentState()+" ").append(getPaymentMethod()+" ")
					.append(getUserId()+" ").append(getPurchaser());
		return sb.toString();
	}
	
	public String getOrderId() {
		return OrderId;
	}

	public BigDecimal getTotalPrice() {
		return TotalPrice;
	}

	public LocalDateTime getOrderDate() {
		return OrderDate;
	}

	public String getPaymentState() {
		return PaymentState;
	}

	public String getPaymentMethod() {
		return PaymentMethod;
	}

	public String getUserId() {
		return UserId;
	}

	public String getPurchaser() {
		return Purchaser;
	}
}
