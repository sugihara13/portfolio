package wireboutique.beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

//wrapping HashMap<ProductBean	,Integer>
public class CartBean extends HashMap<ProductBean , Integer> implements Serializable{
	private static final long serialVersionUID = 1327601025313969029L;

	public CartBean() {
		super();
	}
	
	public BigDecimal value(ProductBean product) {
		if(this.containsKey(product)) {
			BigDecimal quantity = new BigDecimal( this.get(product));
			BigDecimal value = product.IncTaxPrice().multiply(quantity);
			
			return value;
		}
		return null;
	}
	
	public BigDecimal total() {
		BigDecimal totalValue = new BigDecimal(0);
		for(Map.Entry<ProductBean, Integer> item:this.entrySet()) {
			BigDecimal quantity = new BigDecimal( item.getValue());
			BigDecimal value = item.getKey().IncTaxPrice().multiply(quantity);
			
			totalValue = totalValue.add(value);
		}
		
		return totalValue;
	}
}
