package wireboutique.dataaccess.database;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import wireboutique.beans.OrderBean;
import wireboutique.beans.OrderDetailBean;
import wireboutique.beans.ProductBean;
import wireboutique.bo.Payment.PaymentMethod;
import wireboutique.bo.Payment.PaymentState;
import wireboutique.dataaccess.database.tables.OrderDetailsTable;
import wireboutique.dataaccess.database.tables.OrdersTable;


public class OrdersDAO extends DatabaseDAO{
	
	public OrderBean getOrder(String requestId) {
		//sql----------------
		StringBuilder sql = new StringBuilder("SELECT * FROM ");
		sql.append(OrdersTable.ORDER_ID.tableName()).append(" WHERE ");
		sql.append(OrdersTable.ORDER_ID.column()).append(" = ? ;");
		//------------------
		
		//data accsess---------------------
		try(Connection conn = DriverManager.getConnection(Url,User,Pass)){
			PreparedStatement pStm = conn.prepareStatement(sql.toString());
			pStm.setString(1, requestId);
			
			ResultSet rs = pStm.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			
			String orderId = null;
			BigDecimal totalPrice = null;
			Date dtOrderDate= new Date();
			String stPaymentState="";
			String stPaymentMethod="";
			String userId = "";
			String purchaser = null;
			
			rs.next();
			
			//mapping----------------------
			for(int i=1;i<=rsmd.getColumnCount();i++) {
				//OrdersTable column = OrdersTable.getColumn(rsmd.getColumnName(i));
				orderId =rs.getString(OrdersTable.ORDER_ID.column());
				totalPrice = rs.getBigDecimal(OrdersTable.TOTAL_PRICE.column());
				dtOrderDate = rs.getDate(OrdersTable.ORDER_DATE.column());
				stPaymentState = rs.getString(OrdersTable.PAYMENT_STATE.column());
				stPaymentMethod = rs.getString(OrdersTable.PAYMENT_METHODE.column());
				userId = rs.getString(OrdersTable.USER_ID.column());
				purchaser = rs.getString(OrdersTable.PURCHASER.column());
			}
			//-----------------------------	
		
			//building object--------------
			//Date->LocalDatetime
			LocalDateTime orderDate = new Timestamp(dtOrderDate.getTime()).toLocalDateTime();
			//PaymentData -> PaymentEnumObject
			/*orderTablelog故にオブジェクトにマッピングするよりStringのままのがいいかも
			PaymentState paymentState = PaymentState.toState(stPaymentState);
			PaymentMethod paymentMethod = PaymentMethod.toMethod(stPaymentMethod);
			*/
			
			OrderBean order =  new OrderBean(orderId,totalPrice,orderDate,stPaymentState,stPaymentMethod,userId,purchaser);
			System.out.println("OrdersDAO getOrder: " + order);
			return order;
			
			}
		catch(SQLException e){
			e.printStackTrace();
			return new OrderBean();
		}
	}
	
	public ArrayList<OrderDetailBean> getOrderDetails(String reqOrderId) {
		//sql----------------
		StringBuilder sql = new StringBuilder("SELECT * FROM ");
		sql.append(OrderDetailsTable.ORDER_ID.tableName()).append(" WHERE ");
		sql.append(OrderDetailsTable.ORDER_ID.column()).append(" = ? ;");
		//------------------
		
		//data accsess---------------------
		try(Connection conn = DriverManager.getConnection(Url,User,Pass)){
			PreparedStatement pStm = conn.prepareStatement(sql.toString());
			pStm.setString(1, reqOrderId);
			
			ResultSet rs = pStm.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			
			ArrayList<OrderDetailBean> details = new ArrayList<OrderDetailBean>();
			
			while(rs.next()) {
				String orderId = null;
				String productId = null;
				String productName = null;
				BigDecimal purchasePrice = null;
				int quantity = 0;
				BigDecimal taxRate = null;
			
				//mapping----------------------
				for(int i=1;i<=rsmd.getColumnCount();i++) {
					orderId = rs.getString(OrderDetailsTable.ORDER_ID.column());
					productId = rs.getString(OrderDetailsTable.PRODUCT_ID.column());
					productName = rs.getString(OrderDetailsTable.PRODUCT_NAME.column());
					purchasePrice = rs.getBigDecimal(OrderDetailsTable.PURCHASE_PRICE.column());
					quantity = rs.getInt(OrderDetailsTable.QUANTITY.column());
					taxRate = rs.getBigDecimal(OrderDetailsTable.TAX_RATE.column());
				}
			//-----------------------------	
		
			//building object--------------
	
			OrderDetailBean detail =  new OrderDetailBean(orderId,productId,productName,purchasePrice,quantity,taxRate);
			details.add(detail);		
			}
			
			System.out.println("OrderDAO getOrderDetails line: " + details.size());
			for(OrderDetailBean d:details)
				System.out.println("OrderDAO getOrderDetails detail: " + d.toString());
			
			return details;
			
			}
		catch(SQLException e){
			e.printStackTrace();
			return new ArrayList<OrderDetailBean>();
		}
	}
	
	//orderとdatailのDBへの登録 (トランザクション)
	public int setOrder(OrderBean order, List<OrderDetailBean> details) {
		int result = -1;
		try(Connection conn = DriverManager.getConnection(Url,User,Pass)){
			//auto-commitを止める
			//order と detail すべてを更新できたらコミット できなければロールバック
			conn.setAutoCommit(false);
			
			//order
			{
				StringBuilder sql =new StringBuilder("INSERT INTO "+OrdersTable.ORDER_ID.tableName()+" (");
				
				for(OrdersTable column:OrdersTable.values()) {
					sql.append(column.toString());
					if(column.getIdx()!=OrdersTable.values().length)
						sql.append(", ");
				}
				sql.append(") VALUES (?");
				for(int i=0;i<OrdersTable.values().length-1;i++)
					sql.append(",?");
				sql.append(")");
				
				//sql
				//System.out.println("OrdersDAO setOrder sql;"+sql);
				
				PreparedStatement pStm = conn.prepareStatement(sql.toString());
				
				pStm.setString(OrdersTable.ORDER_ID.getIdx(), order.getOrderId());
				pStm.setBigDecimal(OrdersTable.TOTAL_PRICE.getIdx(), order.getTotalPrice());
				pStm.setObject(OrdersTable.ORDER_DATE.getIdx(), order.getOrderDate());
				pStm.setString(OrdersTable.PAYMENT_STATE.getIdx(), order.getPaymentState());
				pStm.setString(OrdersTable.PAYMENT_METHODE.getIdx(), order.getPaymentMethod());
				pStm.setString(OrdersTable.USER_ID.getIdx(), order.getUserId());
				pStm.setString(OrdersTable.PURCHASER.getIdx(), order.getPurchaser());
			
				result = pStm.executeUpdate();
				System.out.println("OrdersDAO setOrder Insert order result: "+result);
				
			}
			
			//orderが登録できていればdetail のinsertをする
			if(result == 1) {
				StringBuilder sql =new StringBuilder("INSERT INTO "+OrderDetailsTable.ORDER_ID.tableName()+" (");
				for(OrderDetailsTable column:OrderDetailsTable.values()) {
					sql.append(column.toString());
					if(column.getIdx()!=OrderDetailsTable.values().length)
						sql.append(", ");
				}
				sql.append(") VALUES (?");
				for(int i=0;i<OrderDetailsTable.values().length-1;i++)
					sql.append(",?");
				sql.append(")");
				
				PreparedStatement pStm = conn.prepareStatement(sql.toString());
				for(OrderDetailBean detail:details) {
					pStm.setString(OrderDetailsTable.ORDER_ID.getIdx(), detail.getOrderId());
					pStm.setString(OrderDetailsTable.PRODUCT_ID.getIdx(), detail.getProductId());
					pStm.setString(OrderDetailsTable.PRODUCT_NAME.getIdx(), detail.getProductName());
					pStm.setBigDecimal(OrderDetailsTable.PURCHASE_PRICE.getIdx(), detail.getPurchasePrice());
					pStm.setInt(OrderDetailsTable.QUANTITY.getIdx(), detail.getQuantity());
					pStm.setBigDecimal(OrderDetailsTable.TAX_RATE.getIdx(), detail.getTaxRate());
				
					result += pStm.executeUpdate();
					System.out.println("OrdersDAO setOrder Insert detail result: "+result);
				}
			}
			else {
				conn.rollback();
				System.out.println("OrdersDAO setOrder faild registration order  rollback: "+result);
			}
			
			//reult がdetailsの件数+orderTableへの1件であればcommit
			if(result == details.size() + 1) {
				conn.commit();
				System.out.println("OrdersDAO setOrder commit : "+result);
			}
			else {
				conn.rollback();
				System.out.println("OrdersDAO setOrder rollback: "+result);
				result=0;
			}
		}
		catch(SQLException e){
			e.printStackTrace();
			return -1;
		}
		
		System.out.println("OrdersDAO setOrder line: "+result+" Order: "+order);
		for(OrderDetailBean detail:details)
			System.out.println("OrderDetails: "+detail);
		
		return result;
	}
	
	public int setOrder(OrderBean order) {
		int result = -1;
		try(Connection conn = DriverManager.getConnection(Url,User,Pass)){
			StringBuilder sql =new StringBuilder("INSERT INTO "+OrdersTable.ORDER_ID.tableName()+" (");
			for(OrdersTable column:OrdersTable.values()) {
				sql.append(column.toString());
				if(column.getIdx()!=OrdersTable.values().length)
					sql.append(", ");
			}
			sql.append(") VALUES (?");
			for(int i=0;i<OrdersTable.values().length-1;i++)
				sql.append(",?");
			sql.append(")");
			
			PreparedStatement pStm = conn.prepareStatement(sql.toString());
				pStm.setString(OrdersTable.ORDER_ID.getIdx(), order.getOrderId());
				pStm.setBigDecimal(OrdersTable.TOTAL_PRICE.getIdx(), order.getTotalPrice());
				java.sql.Date date = java.sql.Date.valueOf(order.getOrderDate().toLocalDate());
				pStm.setDate(OrdersTable.ORDER_DATE.getIdx(), date);
				pStm.setString(OrdersTable.PAYMENT_STATE.getIdx(), order.getPaymentState());
				pStm.setString(OrdersTable.PAYMENT_METHODE.getIdx(), order.getPaymentMethod());
				pStm.setString(OrdersTable.USER_ID.getIdx(), order.getUserId());
				pStm.setString(OrdersTable.PURCHASER.getIdx(), order.getPurchaser());
			
			result = pStm.executeUpdate();
		}
		catch(SQLException e){
			e.printStackTrace();
			return -1;
		}
		System.out.println("OrdersDAO setOrder line: "+result+" Order: "+order);
		
		return result;
	}
	
	public int setOrderDetails(List<OrderDetailBean> details) {
		int result = 0;
		try(Connection conn = DriverManager.getConnection(Url,User,Pass)){
			StringBuilder sql =new StringBuilder("INSERT INTO "+OrderDetailsTable.ORDER_ID.tableName()+" (");
			for(OrdersTable column:OrdersTable.values()) {
				sql.append(column.toString());
				if(column.getIdx()!=OrdersTable.values().length)
					sql.append(", ");
			}
			sql.append(") VALUES (?");
			for(int i=0;i<OrdersTable.values().length-1;i++)
				sql.append(",?");
			sql.append(")");
			
			PreparedStatement pStm = conn.prepareStatement(sql.toString());
			for(OrderDetailBean detail:details) {
				pStm.setString(OrderDetailsTable.ORDER_ID.getIdx(), detail.getOrderId());
				pStm.setString(OrderDetailsTable.PRODUCT_ID.getIdx(), detail.getProductId());
				pStm.setString(OrderDetailsTable.PRODUCT_NAME.getIdx(), detail.getProductName());
				pStm.setBigDecimal(OrderDetailsTable.PURCHASE_PRICE.getIdx(), detail.getPurchasePrice());
				pStm.setInt(OrderDetailsTable.QUANTITY.getIdx(), detail.getQuantity());
				pStm.setBigDecimal(OrderDetailsTable.TAX_RATE.getIdx(), detail.getTaxRate());
			}
			result += pStm.executeUpdate();
		}
		catch(SQLException e){
			e.printStackTrace();
			return -1;
		}
		
		System.out.println("OrdersDAO setOrderDetails line: "+result);
		for(OrderDetailBean detail:details)
			System.out.println("OrderDetails: "+detail);
		
		return result;
	}
}
