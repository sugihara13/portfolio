package wireboutique.beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Objects;


public class ProductBean implements Serializable{
	
	private static final long serialVersionUID = 4161160839621334064L;
	//field private
	private String Id;
	private String Name;
	private String Category;
	private String Manufacturer;
	private BigDecimal ListPrice = BigDecimal.ZERO;
	private TaxBean TaxCategory;
	private LocalDateTime ReleaseDate;
	private String ContentURL;
	private Integer Stock;
	private Boolean IsPublic;
	
	public ProductBean() {
		Id="";
		Name="";
		Category="";
		Manufacturer="";
		ReleaseDate=LocalDateTime.MIN;
		ListPrice = BigDecimal.ZERO;
		TaxCategory = new TaxBean(0, "", BigDecimal.ZERO);
		Stock = 0;
		IsPublic = false;
	}
	
	public ProductBean(String id, String name, String category, String manufacturer,
						BigDecimal listprice,TaxBean tax, LocalDateTime releasedate,
						String contenturl,Integer stock,Boolean isPublic) {
		Id=id;
		Name=name;
		Category=category;
		Manufacturer=manufacturer;
		ReleaseDate=releasedate;
		ListPrice=listprice;
		TaxCategory = tax;
		ContentURL=contenturl;
		Stock=stock;
		IsPublic=isPublic;
	}
	
	@Override
	public String toString() {
		StringBuilder sb =new StringBuilder();
		sb.append(getId()+" ").append(getName()+" ").append(getCategory()+" ")
					.append(getManufacturer()+" ").append(getReleaseDate()+" ")
					.append(getListPrice()+" ").append(getContentURL());
		return sb.toString();
	}
	
	@Override
	public boolean equals(Object object) {
		if(object instanceof ProductBean) {
			if(Id.equals(((ProductBean) object).getId()))
				return true;
			else return false;
		}
		else return false;
	}
	
	@Override public int hashCode() {
		int prime = 23;
	    return prime * Objects.hash(Id);
	}
	
	//setter getter
	public void setName(String name) {
		Name=name;
	}
	public String getName() {
		return Name;
	}

	public String getCategory() {
		return Category;
	}
	public void setCategory(String category) {
		Category = category;
	}
	public LocalDateTime getReleaseDate() {
		return ReleaseDate;
	}
	public void setReleaseDate(LocalDateTime releasedate) {
		ReleaseDate = releasedate;
	}
	public String getId() {
		return Id;
	}
	public void setId(String productId) {
		Id = productId;
	}
	public String getManufacturer() {
		return Manufacturer;
	}
	public void setManufacturer(String manufacturer) {
		Manufacturer = manufacturer;
	}

	public BigDecimal getListPrice() {
		return ListPrice;
	}

	public void setListPrice(BigDecimal listPrice) {
		ListPrice = listPrice;
	}

	public String getContentURL() {
		return ContentURL;
	}

	public void setContentURL(String contentURL) {
		ContentURL = contentURL;
	}
	
public TaxBean getTaxCategory() {
		return TaxCategory;
	}
	
	public void setTaxCategory(TaxBean tax) {
		TaxCategory = tax;
	}
	
	public BigDecimal getTaxRate() {
		return TaxCategory.getRate();
	}

	public Integer getStock() {
		return Stock;
	}

	public void setStock(Integer stock) {
		Stock = stock;
	}
	
	public BigDecimal IncTaxPrice(){
		BigDecimal price,tax;
		//端数は切り捨てに
		if(TaxCategory.getRate().compareTo(BigDecimal.ZERO) > 0) {
			tax = ListPrice.multiply(TaxCategory.getRate());
			price = ListPrice.add(tax);
			//小数切り捨て
			price = price.setScale(0,RoundingMode.DOWN);
			
			return price;
		}
		else {
			return ListPrice.setScale(0);
		}
	}

	public Boolean getIsPublic() {
		return IsPublic;
	}

	public void setIsPublic(Boolean isPublic) {
		this.IsPublic = isPublic;
	}
}