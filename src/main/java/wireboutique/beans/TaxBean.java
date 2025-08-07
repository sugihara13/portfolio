package wireboutique.beans;

import java.math.BigDecimal;

public class TaxBean {
	private final int Id;
	private final String Category;
	private final BigDecimal Rate;
	
	public TaxBean(int id, String category, BigDecimal rate) {
		Id = id;
		Category = category;
		Rate = rate;
	}

	public String getCategory() {
		return Category;
	}

	public BigDecimal getRate() {
		return Rate;
	}

	public int getId() {
		return Id;
	}
}
