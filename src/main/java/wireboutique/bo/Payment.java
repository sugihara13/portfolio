package wireboutique.bo;



//payment proc 処理は制作しなくていいか
//実際なら決済システムとのやりとりをするクラスになるか
public class Payment {
	public enum PaymentState{
		UNREGISTERED("Unregistered"),
		WAITING_RESPONSE("WaitingResponse"),
		COMPLETED("Completed"),
		FAILED("Failed"),
		UNDEFIND("Undefind");
		
		private final String StateName;
		
		private PaymentState(String stateName){
			this.StateName=stateName;
		}
		
		public static PaymentState toState(String methodName) {
			for(PaymentState v:values()) {
				if(v.toString().equals(methodName))
					return v;
			}
			return UNDEFIND;
		}
		
		@Override
		public String toString() {
			return StateName;
		}
	}
	
	public enum PaymentMethod{
		CARD("Card"),
		BANK("BankTransfer"),
		UNDEFIND("undefind");
		
		private final String MethodName;
		
		private PaymentMethod(String methodName){
			this.MethodName=methodName;
		}
		
		public static PaymentMethod toMethod(String methodName) {
			for(PaymentMethod v:values()) {
				if(v.toString().equals(methodName))
					return v;
			}
			return UNDEFIND;
		}
		
		@Override
		public String toString() {
			return MethodName;
		}
	}
	
	public Payment() {
		// TODO 自動生成されたコンストラクター・スタブ
	}
	
	public void registPayment(String paymentMethod) {
		
	}
	
	//payment に対応したtokenが決済システムからくるならそれでState取得
	public PaymentState getState() {
		return PaymentState.COMPLETED;
	}
}
