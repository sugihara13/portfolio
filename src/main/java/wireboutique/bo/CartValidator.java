package wireboutique.bo;

import java.time.LocalDateTime;
import java.util.HashMap;

import wireboutique.beans.CartBean;
import wireboutique.beans.ProductBean;
import wireboutique.dataaccess.database.ProductDAO;


//コンストラクタに渡されたCartの中のproductをdatabeseから取得しそれらが注文できるかを基準に判定する
public class CartValidator {
	private CartBean Cart;
	private boolean cartIsValid = false;
	
	private HashMap<String, String> ivProducts = null;
	
	public CartValidator(CartBean cart) {
		Cart = cart;
		ivProducts = new HashMap<String, String>();
		
		validate();
	}
	
	private void validate() {
		if(Cart.size() > 0) {
			ProductDAO productDao = new ProductDAO();
			
			for(CartBean.Entry<ProductBean,Integer> cartProduct :Cart.entrySet()) {
				var p= productDao.getProduct(cartProduct.getKey().getId(), false);
				
				StringBuilder msg = new StringBuilder();
				if(p instanceof ProductBean) {
					//公開されていなければ購入不可
					if(p.getIsPublic() == false) {
						msg.append(p.getName()).append(" is invalid.");
						cartIsValid=false;
					}
					//リリース後でなければ購入不可
					else if(p.getReleaseDate().isAfter(LocalDateTime.now())) {
						msg.append(p.getName()).append(" is not released.");
						cartIsValid=false;
					}
					//Stockに関してはnull==在庫制限なし null != 在庫まで購入可能として判定
					else if((Integer)p.getStock() instanceof Integer) {
						if(p.getStock() < cartProduct.getValue()) {
							msg.append(p.getName()).append(" is out of stock.");
							
							ivProducts.put(p.getName(), msg.toString());
							cartIsValid=false;
						}
						else cartIsValid = true;
					}
					else cartIsValid = true;
				}
				else {
					msg.append(p.getName()).append(" is invalid.");
					cartIsValid=false;
				}
			}
		}
	}
	
	public boolean isValid() {
		return cartIsValid;
	}
	
	/*
	 * Key ProductId
	 * Value message
	 * */
	public HashMap<String, String> invalidProducts() {
		return ivProducts;
	}
}
