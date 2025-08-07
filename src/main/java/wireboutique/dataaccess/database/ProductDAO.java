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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.time.LocalDateTime;

import wireboutique.beans.ProductBean;
import wireboutique.beans.TaxBean;
import wireboutique.dataaccess.database.tables.*;

public class ProductDAO extends DatabaseDAO{
	//このDAOで扱うsql resut column定義
	public enum Column {
		PRODUCT_ID(1,ProductsTable.PRODUCT_ID.column()),
		NAME(2,ProductsTable.NAME.column()),
		CATEGORY(3,CategoriesTable.CATEGORY.column()),
		MANUFACTURER(4,ManufacturersTable.MANUFACTURER.column()),
		LIST_PRICE(5,ProductsTable.LIST_PRICE.column()),
		RELEASE_DATE(6,ProductsTable.RELEASE_DATE.column()),
		TAX_CATEGORY_ID(7,ProductsTable.TAX_CATEGORY_ID.column()),
		TAX_RATE(8,TaxCategoriesTable.TAX_RATE.column()),
		TAX_NAME(9,TaxCategoriesTable.TAX_NAME.column()),
		CONTENTS(10,ProductsTable.CONTENTS.column()),
		STOCK(11,ProductsTable.STOCK.column()),
		IS_PUBLIC(12,ProductsTable.IS_PUBLIC.column());
		
		private final int Idx;
		private final String Name;
		
		private Column(int Idx,String name) {
			this.Idx=Idx;
			this.Name=name;
		}
		
		public int getIdx() {
			return this.Idx;
		}
		public String getName() {
			return this.Name;
		}
		
		public static Column getColumn(String columnName) {
			for(Column v:values()) {
				if(v.getName().equals(columnName))
					return v;
			}
			return null;
		}
	}
	
	//件数とソート順指定 keyに一致するcolomnでソート key無効の場合IDで
	public ArrayList<ProductBean>  getProducts(String key, int recordcount, boolean order, long offset, boolean onlyPublicProduct) {
		//build sql for preparedstatement------------
		//sortkey default=ID
		String sortkey=Column.PRODUCT_ID.getName();
		for(Column column:Column.values()) {
			//column にkeyがあれば有効
			if(column.getName().equals(key))
				sortkey=key+" ";
		}
		//result recordcount
		String sqlcount;
		sqlcount = String.valueOf(recordcount);
		//order
		String sqlorder;
		if(order==ASC) sqlorder="ASC";
		else sqlorder="DESC ";
		
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append("p."+Column.PRODUCT_ID.toString())
			.append(",p."+Column.NAME.toString())
			.append(",c."+Column.CATEGORY.toString())
			.append(",m."+Column.MANUFACTURER.toString())
			.append(",p."+Column.RELEASE_DATE.toString())
			.append(",p."+Column.LIST_PRICE.toString())
			.append(",p."+Column.TAX_CATEGORY_ID.toString())
			.append(",t."+Column.TAX_RATE.toString())
			.append(",t."+Column.TAX_NAME.toString())
			.append(",p."+Column.CONTENTS.toString())
			.append(",p."+Column.STOCK.toString())
			.append(",p."+Column.IS_PUBLIC.toString())
			.append(" FROM products p")
			.append(" INNER JOIN categories c ON p."+ProductsTable.CATEGORY_ID.column()).append(" = c."+CategoriesTable.CATEGORY_ID.column())
			.append(" INNER JOIN manufacturers m ON p."+ProductsTable.MANUFACTURER_ID.column()).append(" = m."+ManufacturersTable.MANUFACTURER_ID.column())
			.append(" INNER JOIN  tax_categories t ON p."+ProductsTable.TAX_CATEGORY_ID.column()).append(" = t."+TaxCategoriesTable.TAX_CATEGORY_ID.column());
		
		if(onlyPublicProduct)
			sql.append(" WHERE ").append("p.").append(Column.IS_PUBLIC).append(" = ").append(true);
		
		sql.append(" ORDER BY p.");
		sql.append(sortkey).append(sqlorder);
		
		sql.append(" LIMIT ").append(sqlcount).append(" OFFSET ").append(offset);
		//-------------------------------------------
		
		ArrayList<ProductBean> productList = new ArrayList<ProductBean>();
		
		try(Connection conn = DriverManager.getConnection(Url,User,Pass)){
			PreparedStatement pStm = conn.prepareStatement(sql.toString());
			
			ResultSet rs = pStm.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			
			while (rs.next()) {
				//product ProductsTable
				int id=0;
				String name = "", category = "", manufacturer = "";
				LocalDateTime releasedate = null;
				BigDecimal listprice = new BigDecimal(0);
				String contentsurl = "";
				Date date = new Date();
				int taxId = -1;
				BigDecimal taxRate = new BigDecimal(0);
				String taxCategory = "";
				Integer stock = 0;
				Boolean isPublic = false;
				
				//ProductsTable と同じテーブル名から getObject
				for(int i=1;i<=rsmd.getColumnCount();i++) {
					Column column = Column.getColumn(rsmd.getColumnName(i));
					
					switch(column) {
						//case = Column Name
						case PRODUCT_ID:	id = rs.getInt(i); break;
						case NAME: name = rs.getString(i); break;
						case CATEGORY: category = rs.getString(i); break;
						case MANUFACTURER: manufacturer = rs.getString(i); break;
						case RELEASE_DATE: date = rs.getDate(i); break;
						case LIST_PRICE:	listprice = rs.getBigDecimal(i); break;
						case CONTENTS:	contentsurl = rs.getString(i); break;
						case TAX_CATEGORY_ID:	taxId = rs.getInt(i); break;
						case TAX_RATE:	taxRate = rs.getBigDecimal(i); break;
						case TAX_NAME:	taxCategory = rs.getString(i); break;
						case STOCK:{
							stock = rs.getInt(i);
							if(rs.wasNull())
								stock = null;
							break;
						}
						case IS_PUBLIC: isPublic = rs.getBoolean(i); break;
					}
				}
				
				//Date->LocalDatetime
				releasedate = new Timestamp(date.getTime()).toLocalDateTime();
				//IdをStringとして扱う
				String idtoStoring = String.valueOf(id);
				
				TaxBean tax = new TaxBean(taxId, taxCategory, taxRate);
				
				ProductBean product = new ProductBean(idtoStoring,name,category,manufacturer,listprice,tax,releasedate,contentsurl,stock,isPublic);
				productList.add(product);
			}
		}
		catch(SQLException e){
			e.printStackTrace();
			productList = new ArrayList<ProductBean>();
			return null;
		}
		
		return productList;
	}
	
	//ID(dbの主キー)のproductをtableから取得
	public ProductBean  getProduct(String productId, boolean onlyPublicProduct) {
		//build sql for preparedstatement
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append("p."+Column.PRODUCT_ID.toString())
			.append(",p."+Column.NAME.toString())
			.append(",c."+Column.CATEGORY.toString())
			.append(",m."+Column.MANUFACTURER.toString());
		sql.append(",p."+Column.RELEASE_DATE.toString())
			.append(",p."+Column.LIST_PRICE.toString())
			.append(",p."+Column.TAX_CATEGORY_ID.toString())
			.append(",t."+Column.TAX_RATE.toString())
			.append(",t."+Column.TAX_NAME.toString())
			.append(",p."+Column.CONTENTS.toString())
			.append(",p."+Column.STOCK.toString())
			.append(",p."+Column.IS_PUBLIC.toString())
			.append(" FROM products p");
		sql.append(" INNER JOIN categories c ON p."+ProductsTable.CATEGORY_ID.column()).append(" = c."+CategoriesTable.CATEGORY_ID.column())
			.append(" INNER JOIN manufacturers m ON p."+ProductsTable.MANUFACTURER_ID.column()).append(" = m."+ManufacturersTable.MANUFACTURER_ID.column())
			.append(" INNER JOIN  tax_categories t ON p."+ProductsTable.TAX_CATEGORY_ID.column()).append(" = t."+TaxCategoriesTable.TAX_CATEGORY_ID.column());
		sql.append(" WHERE p."+Column.PRODUCT_ID.toString()+" = ").append("?");
		
		if(onlyPublicProduct)
			sql.append(" AND ").append("p.").append(Column.IS_PUBLIC).append(" = ").append(true);
		
		ProductBean product = null;
		
		try(Connection conn = DriverManager.getConnection(Url,User,Pass)){
			PreparedStatement pStm = conn.prepareStatement(sql.toString());
			
			pStm.setString(1,productId);
			
			ResultSet rs = pStm.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			
			//product param
			int id=0;
			String name="", category="", manufacturer="";
			LocalDateTime releasedate = null;
			BigDecimal listprice=new BigDecimal(0);String contentsurl="";
			Date date = new Date();
			int taxId = -1;
			BigDecimal taxRate = new BigDecimal(0);
			String taxCategory = "";
			Integer stock = 0;
			Boolean isPublic = false;
				
			//param と同じテーブル名から getObject
			if(rs.next()) {
			
				for(int i=1;i<=rsmd.getColumnCount();i++) {
					//Column column = Column.valueOf(rsmd.getColumnName(i));
					
					//このDAOのTable定数にresultSetのcolumnNameをマッピング
					Column column = Column.getColumn(rsmd.getColumnName(i));
					switch(column) {
						//case = Column Name
						case PRODUCT_ID:	id = rs.getInt(i); break;
						case NAME: name = rs.getString(i); break;
						case CATEGORY: category = rs.getString(i); break;
						case MANUFACTURER: manufacturer = rs.getString(i); break;
						case RELEASE_DATE: date = rs.getDate(i); break;
						case LIST_PRICE:	listprice = rs.getBigDecimal(i); break;
						case CONTENTS:	contentsurl = rs.getString(i); break;
						case TAX_CATEGORY_ID:	taxId = rs.getInt(i); break;
						case TAX_RATE:	taxRate = rs.getBigDecimal(i); break;
						case TAX_NAME:	taxCategory = rs.getString(i); break;
						case STOCK:{	
							stock = rs.getInt(i);
							if(rs.wasNull())
								stock = null;
							break;
						}
						case IS_PUBLIC: isPublic = rs.getBoolean(i); break;
					}
				}
			
				//Date->LocalDatetime
				releasedate = new Timestamp(date.getTime()).toLocalDateTime();
				//IdをStringとして扱う
				String idtoStoring = String.valueOf(id);
				
				//priceは小数を扱わない (切り捨て)
				listprice.setScale(0,RoundingMode.DOWN);
				
				TaxBean tax = new TaxBean(taxId, taxCategory, taxRate);
				
				product = new ProductBean(idtoStoring,name,category,manufacturer,listprice,tax,releasedate,contentsurl,stock,isPublic);

				
			}
			return product;
		}
		catch(SQLException e){
			e.printStackTrace();
			product = null;
			return product;
		}
	}
	
	//product.nameを検索する
	public ArrayList<ProductBean>  searchProducts(String keywords, int recordcount, boolean order, long offset, boolean onlyPublicProduct) {
		//get words
		ArrayList<String> words = new ArrayList<String>();
		{
			Pattern pattern = Pattern.compile("[^\\s_%]{1,200}");
			Matcher matcher	= pattern.matcher(keywords);
			while(matcher.find()) {
				words.add(matcher.group());
			}
		}
		
		//build sql for preparedstatement------------
		//sortkey
		String sortkey=Column.RELEASE_DATE.getName();

		//result recordcount
		String sqlcount;
		sqlcount = String.valueOf(recordcount);
		//order
		String sqlorder;
		if(order==ASC) sqlorder="ASC";
		else sqlorder="DESC ";
		
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append("p."+Column.PRODUCT_ID.toString())
			.append(",p."+Column.NAME.toString())
			.append(",c."+Column.CATEGORY.toString())
			.append(",m."+Column.MANUFACTURER.toString())
			.append(",p."+Column.RELEASE_DATE.toString())
			.append(",p."+Column.LIST_PRICE.toString())
			.append(",p."+Column.TAX_CATEGORY_ID.toString())
			.append(",t."+Column.TAX_RATE.toString())
			.append(",t."+Column.TAX_NAME.toString())
			.append(",p."+Column.CONTENTS.toString())
			.append(",p."+Column.STOCK.toString())
			.append(",p."+Column.IS_PUBLIC.toString())
			.append(" FROM products p")
			.append(" INNER JOIN categories c ON p."+ProductsTable.CATEGORY_ID.column()).append(" = c."+CategoriesTable.CATEGORY_ID.column())
			.append(" INNER JOIN manufacturers m ON p."+ProductsTable.MANUFACTURER_ID.column()).append(" = m."+ManufacturersTable.MANUFACTURER_ID.column())
			.append(" INNER JOIN  tax_categories t ON p."+ProductsTable.TAX_CATEGORY_ID.column()).append(" = t."+TaxCategoriesTable.TAX_CATEGORY_ID.column());
		
		if(words.size()>0) {
			sql.append(" WHERE (");

			for(int i = 0;i < words.size()-1;i++)
					sql.append("p.").append(ProductsTable.NAME.column()).append(" LIKE ").append("?")
						.append(" AND ");
			sql.append("p.").append(ProductsTable.NAME.column()).append(" LIKE ").append("?");
			
			if(words.size()==1) {
				sql.append(" OR ");
				sql.append("p.").append(ProductsTable.PRODUCT_ID.column()).append(" = ").append("?");
			}
			sql.append(")");
			
			if(onlyPublicProduct)
				sql.append(" AND ").append("p.").append(Column.IS_PUBLIC).append(" = ").append(true);
		}
		else if(words.size()==0) {
			if(onlyPublicProduct)
				sql.append(" WHERE ").append("p.").append(Column.IS_PUBLIC).append(" = ").append(true);
		}
		
		sql.append(" ORDER BY p.");
		sql.append(sortkey).append(" ").append(sqlorder);
		
		sql.append(" LIMIT ").append(sqlcount).append(" OFFSET ").append(offset);
		//-------------------------------------------
		
		ArrayList<ProductBean> productList = new ArrayList<ProductBean>();
		
		try(Connection conn = DriverManager.getConnection(Url,User,Pass)){
			PreparedStatement pStm = conn.prepareStatement(sql.toString());
			
			for(int i=1;i<=words.size();i++)
				pStm.setString(i, "%"+words.get(i-1)+"%");
			
			if(words.size()==1)
				pStm.setString(2, words.get(0));
			
			ResultSet rs = pStm.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			
			
			while (rs.next()) {
				//product ProductsTable
				int id=0;
				String name = "", category = "", manufacturer = "";
				LocalDateTime releasedate = null;
				BigDecimal listprice = new BigDecimal(0);
				String contentsurl = "";
				Date date = new Date();
				int taxId = -1;
				BigDecimal taxRate = new BigDecimal(0);
				String taxCategory = "";
				Integer stock = 0;
				Boolean isPublic = false;
				
				//ProductsTable と同じテーブル名から getObject
				for(int i=1;i<=rsmd.getColumnCount();i++) {
					Column column = Column.getColumn(rsmd.getColumnName(i));
					
					switch(column) {
						//case = Column Name
						case PRODUCT_ID:	id = rs.getInt(i); break;
						case NAME: name = rs.getString(i); break;
						case CATEGORY: category = rs.getString(i); break;
						case MANUFACTURER: manufacturer = rs.getString(i); break;
						case RELEASE_DATE: date = rs.getDate(i); break;
						case LIST_PRICE:	listprice = rs.getBigDecimal(i); break;
						case CONTENTS:	contentsurl = rs.getString(i); break;
						case TAX_CATEGORY_ID:	taxId = rs.getInt(i); break;
						case TAX_RATE:	taxRate = rs.getBigDecimal(i); break;
						case TAX_NAME:	taxCategory = rs.getString(i); break;
						case STOCK:{
							stock = rs.getInt(i);
							if(rs.wasNull())
								stock = null;
							break;
						}
						case IS_PUBLIC: isPublic = rs.getBoolean(i); break;
					}
				}
				
				//Date->LocalDatetime
				releasedate = new Timestamp(date.getTime()).toLocalDateTime();
				//IdをStringとして扱う
				String idtoStoring = String.valueOf(id);
				
				TaxBean tax = new TaxBean(taxId, taxCategory, taxRate);
				
				ProductBean product = new ProductBean(idtoStoring,name,category,manufacturer,listprice,tax,releasedate,contentsurl,stock,isPublic);
				productList.add(product);
			}
		}
		catch(SQLException e){
			e.printStackTrace();
			productList = new ArrayList<ProductBean>();
			return null;
		}
		
		return productList;
	}
	
	public String getContentUrl(String productId) {
		//build sql for preparedstatement
			StringBuilder sql = new StringBuilder("SELECT ");
			sql.append( Column.CONTENTS.toString()).append(" FROM products");
			sql.append(" WHERE "+Column.PRODUCT_ID.toString()+" = ").append(productId);
			
			try(Connection conn = DriverManager.getConnection(Url,User,Pass)){
				PreparedStatement pStm = conn.prepareStatement(sql.toString());
				ResultSet rs = pStm.executeQuery();

				rs.next();
				return rs.getString(1);

			}
			catch(SQLException e){
				e.printStackTrace();
				//System.out.println("ProductDAO getContentUrl id: "+productId);
				//System.out.println("ProductDAO getContentUrl sql: "+sql);
				return null;
			}
	}
	
	
	/*
	 * return productId
	 */
	public String setProduct(ProductBean product) {
		int result = -1;
		
		//build sql
		StringBuilder sqlInsertProducts =new StringBuilder("INSERT INTO ");
		
		sqlInsertProducts.append(ProductsTable.PRODUCT_ID.tableName())
			.append(" (");
		
		for(ProductsTable column:ProductsTable.values()) {
			//insertしないcolumn
			if(column == ProductsTable.UPDATED_AT || column == ProductsTable.CREATED_AT) {
				continue;
			}
			else {
				sqlInsertProducts.append(column.toString());
				
				if(column.getIdx() != ProductsTable.values().length)
					sqlInsertProducts.append(", ");
			}
		}
		
		{
			int paramNum = ProductsTable.values().length-2;
			
			sqlInsertProducts.append(") VALUES (?");
			for(int i=0;i<paramNum-1;i++)
				sqlInsertProducts.append(",?");
			sqlInsertProducts.append(")");
		}
		
		StringBuilder sqlInsertProductLogs =new StringBuilder("INSERT INTO ");
		sqlInsertProductLogs.append(ProductLogsTable.PRODUCT_ID.tableName()).append(" (");
		
		for(ProductLogsTable column:ProductLogsTable.values()) {
			sqlInsertProductLogs.append(column.toString());
			
			if(column.getIdx() != ProductLogsTable.values().length)
				sqlInsertProductLogs.append(", ");
		}
		
		{
			int paramNum = ProductLogsTable.values().length;
			
			sqlInsertProductLogs.append(") VALUES (?");
			for(int i=0;i<paramNum-1;i++)
				sqlInsertProductLogs.append(",?");
			sqlInsertProductLogs.append(")");
		}
		
		StringBuilder sqlSelectCategoryId =
				new StringBuilder("SELECT ").append(CategoriesTable.CATEGORY_ID.column()).append(", ").append(CategoriesTable.CATEGORY.column())
						.append(" FROM ").append(CategoriesTable.CATEGORY_ID.tableName())
						.append(" WHERE ").append(CategoriesTable.CATEGORY.column()).append(" = '").append(product.getCategory()).append("';");
		
		StringBuilder sqlSelectManufacturerId =
				new StringBuilder("SELECT ").append(ManufacturersTable.MANUFACTURER_ID.column()).append(", ").append(ManufacturersTable.MANUFACTURER_ID.column())
						.append(" FROM ").append(ManufacturersTable.MANUFACTURER_ID.tableName())
						.append(" WHERE ").append(ManufacturersTable.MANUFACTURER.column()).append(" = '").append(product.getManufacturer()).append("';");

		try(Connection conn = DriverManager.getConnection(Url,User,Pass)){
			conn.setAutoCommit(false);
			PreparedStatement pStm = conn.prepareStatement(sqlInsertProducts.toString());
			
			ResultSet pIdRs=pStm.executeQuery("SELECT "+ProductsTable.PRODUCT_ID.column()+" FROM "+ProductsTable.PRODUCT_ID.tableName()+" ORDER BY "+ProductsTable.PRODUCT_ID.column()+" DESC LIMIT 1 ;");
			pIdRs.next();
			int newProductId =pIdRs.getInt(1)+1;
			
			//外部テーブルから取得
			//toId DBよりappScopeにするか引数で外部からもらうほうがクラス役割的には自然かも
			int cateId=0,manuId=0,taxId=0;

			ResultSet cIdRs=pStm.executeQuery(sqlSelectCategoryId.toString());
			if(cIdRs.next())
				cateId=cIdRs.getInt(1);
			else throw new SQLException("not exist category :"+product.getCategory());
			cIdRs.close();
			
			ResultSet mIdRs=pStm.executeQuery(sqlSelectManufacturerId.toString());
			if(mIdRs.next())
				manuId=mIdRs.getInt(1);
			else throw new SQLException("not exist manufacturer :"+product.getManufacturer());
			mIdRs.close();
			
			java.sql.Date date = java.sql.Date.valueOf(product.getReleaseDate().toLocalDate());

			for(ProductsTable pt:ProductsTable.values()) {
				int idx = pt.getIdx();
				if(pt.getIdx() > ProductsTable.UPDATED_AT.getIdx())
					idx--;
				if(pt.getIdx() > ProductsTable.CREATED_AT.getIdx())
					idx--;
				
				switch (pt) {
				case PRODUCT_ID -> pStm.setInt(idx,newProductId);
				case NAME -> pStm.setString(idx, product.getName());
				case CATEGORY_ID -> pStm.setInt(idx, cateId);
				case MANUFACTURER_ID -> pStm.setInt(idx, manuId);
				case LIST_PRICE -> pStm.setBigDecimal(idx, product.getListPrice());
				case RELEASE_DATE -> pStm.setDate(idx, date);
				case CONTENTS -> pStm.setString(idx, product.getContentURL());
				case TAX_CATEGORY_ID -> pStm.setInt(idx,product.getTaxCategory().getId());
				case STOCK ->{ 
					if(product.getStock() == null) {
						pStm.setNull(idx, java.sql.Types.INTEGER);
					}
					else {
						pStm.setInt(idx, product.getStock());
					}
				}
				case IS_PUBLIC -> pStm.setBoolean(idx, product.getIsPublic());
				case UPDATED_AT,CREATED_AT ->{}
				}
			}
			result = pStm.executeUpdate();
			
			if(result == 1) {
				//insert log
				pStm = conn.prepareStatement("SELECT * FROM products WHERE PRODUCT_ID = "+newProductId);
				ResultSet newProductRs = pStm.executeQuery();
				
				newProductRs.next();
				java.sql.Date updatedAt = newProductRs.getDate(ProductsTable.UPDATED_AT.getIdx());
				
				
				ResultSet pLogIdRs=pStm.executeQuery("SELECT "+ProductLogsTable.PRODUCT_LOG_ID.column()+" FROM product_logs ORDER BY "+ProductLogsTable.PRODUCT_LOG_ID.column()+" DESC LIMIT 1 ;");
				pLogIdRs.next();
				int newLogId =pLogIdRs.getInt(1)+1;
				
				pStm = conn.prepareStatement(sqlInsertProductLogs.toString());
				
				for(ProductLogsTable pt:ProductLogsTable.values()) {
					int idx=pt.getIdx();
					switch (pt) {
						case PRODUCT_LOG_ID -> pStm.setLong(idx, newLogId);
						case PRODUCT_ID -> pStm.setInt(idx,newProductId);
						case UPDATED_AT -> pStm.setDate(idx,updatedAt);
						case NAME -> pStm.setString(idx, product.getName());
						case CATEGORY_ID -> pStm.setInt(idx, cateId);
						case MANUFACTURER_ID -> pStm.setInt(idx, manuId);
						case LIST_PRICE -> pStm.setBigDecimal(idx, product.getListPrice());
						case RELEASE_DATE -> pStm.setDate(idx, date);
						case CONTENTS -> pStm.setString(idx, product.getContentURL());
						case TAX_CATEGORY_ID -> pStm.setInt(idx,product.getTaxCategory().getId());
						case STOCK ->{ 
							if(product.getStock() == null) {
								pStm.setNull(idx, java.sql.Types.INTEGER);
							}
							else {
								pStm.setInt(idx, product.getStock());
							}
						}
						case IS_PUBLIC -> pStm.setBoolean(idx, product.getIsPublic());
					}
				}
				
				result += pStm.executeUpdate();	
			}
			
			if(result == 2) { 
				conn.commit();
				System.out.println("ProductDAO setProduct comit result:"+result);
				System.out.println("ProductDAO setProduct comit productId:"+newProductId);
			}
			else {
				conn.rollback();
				System.out.println("ProductDAO setProduct rollback result:"+result);
				System.out.println("ProductDAO setProduct rollback productId:"+newProductId);
			}
			
			return String.valueOf(newProductId);
		}
		catch(SQLException e){
			e.printStackTrace();
			return "";
		}
	}
	
	public int updateProduct(ProductBean product) {
		int result = -1;
		
		//build sql
		//to update product
		StringBuilder sqlUpdateProducts =new StringBuilder("UPDATE ");
		
		sqlUpdateProducts.append(ProductsTable.PRODUCT_ID.tableName())
			.append(" SET ");
		for(ProductsTable column:ProductsTable.values()) {
			//updateしないcolumn
			if(column == ProductsTable.STOCK || column == ProductsTable.UPDATED_AT || column == ProductsTable.CREATED_AT) {
				continue;
			}
			else {
				sqlUpdateProducts.append(column.toString()).append(" = ?,");
			}
		}
		sqlUpdateProducts.deleteCharAt(sqlUpdateProducts.length()-1);
		
		sqlUpdateProducts.append(" WHERE ")
			.append(ProductsTable.PRODUCT_ID.column())
			.append(" = '").append(product.getId()).append("'");
		
		//to insert product log
		StringBuilder sqlInsertProductLogs =new StringBuilder("INSERT INTO ");
		sqlInsertProductLogs.append(ProductLogsTable.PRODUCT_ID.tableName()).append(" (");
		
		for(ProductLogsTable column:ProductLogsTable.values()) {
			sqlInsertProductLogs.append(column.toString());
			
			if(column.getIdx() != ProductLogsTable.values().length)
				sqlInsertProductLogs.append(", ");
		}
		
		{
			int paramNum = ProductLogsTable.values().length;
			
			sqlInsertProductLogs.append(") VALUES (?");
			for(int i=0;i<paramNum-1;i++)
				sqlInsertProductLogs.append(",?");
			sqlInsertProductLogs.append(")");
		}
		
		StringBuilder sqlSelectCategoryId =
				new StringBuilder("SELECT ").append(CategoriesTable.CATEGORY_ID.column()).append(", ").append(CategoriesTable.CATEGORY.column())
						.append(" FROM ").append(CategoriesTable.CATEGORY_ID.tableName())
						.append(" WHERE ").append(CategoriesTable.CATEGORY.column()).append(" = '").append(product.getCategory()).append("';");
		
		StringBuilder sqlSelectManufacturerId =
				new StringBuilder("SELECT ").append(ManufacturersTable.MANUFACTURER_ID.column()).append(", ").append(ManufacturersTable.MANUFACTURER_ID.column())
						.append(" FROM ").append(ManufacturersTable.MANUFACTURER_ID.tableName())
						.append(" WHERE ").append(ManufacturersTable.MANUFACTURER.column()).append(" = '").append(product.getManufacturer()).append("';");

		try(Connection conn = DriverManager.getConnection(Url,User,Pass)){
			conn.setAutoCommit(false);
			PreparedStatement pStm = conn.prepareStatement(sqlUpdateProducts.toString());
			
			//外部テーブルから取得
			int cateId=0,manuId=0,taxId=0;

			ResultSet cIdRs=pStm.executeQuery(sqlSelectCategoryId.toString());
			if(cIdRs.next())
				cateId=cIdRs.getInt(1);
			else throw new SQLException("not exist category :"+product.getCategory());
			cIdRs.close();
			
			ResultSet mIdRs=pStm.executeQuery(sqlSelectManufacturerId.toString());
			if(mIdRs.next())
				manuId=mIdRs.getInt(1);
			else throw new SQLException("not exist manufacturer :"+product.getManufacturer());
			mIdRs.close();
			
			int productId = Integer.valueOf(product.getId()); 
			java.sql.Timestamp releaseDate = java.sql.Timestamp.valueOf(product.getReleaseDate());
			
			for(ProductsTable pt:ProductsTable.values()) {
				int idx = pt.getIdx();
				if(pt.getIdx() > ProductsTable.UPDATED_AT.getIdx())
					idx--;
				if(pt.getIdx() > ProductsTable.CREATED_AT.getIdx())
					idx--;
				if(pt.getIdx() > ProductsTable.STOCK.getIdx())
					idx--;
				
				switch (pt) {
				case PRODUCT_ID -> pStm.setInt(idx,productId);
				case NAME -> pStm.setString(idx, product.getName());
				case CATEGORY_ID -> pStm.setInt(idx, cateId);
				case MANUFACTURER_ID -> pStm.setInt(idx, manuId);
				case LIST_PRICE -> pStm.setBigDecimal(idx, product.getListPrice());
				case RELEASE_DATE -> pStm.setTimestamp(idx, releaseDate);
				case CONTENTS -> pStm.setString(idx, product.getContentURL());
				case TAX_CATEGORY_ID -> pStm.setInt(idx,product.getTaxCategory().getId());
				case IS_PUBLIC -> pStm.setBoolean(idx, product.getIsPublic());
				case UPDATED_AT,CREATED_AT,STOCK ->{}
				}
			}
			result = pStm.executeUpdate();
			
			if(result == 1) {
				//insert log
				pStm = conn.prepareStatement("SELECT * FROM products WHERE PRODUCT_ID = "+productId);
				ResultSet newProductRs = pStm.executeQuery();
				
				newProductRs.next();
				java.sql.Timestamp updatedAt = newProductRs.getTimestamp(ProductsTable.UPDATED_AT.getIdx());
				
				ResultSet pLogIdRs=pStm.executeQuery("SELECT "+ProductLogsTable.PRODUCT_LOG_ID.column()+" FROM product_logs ORDER BY "+ProductLogsTable.PRODUCT_LOG_ID.column()+" DESC LIMIT 1 ;");
				pLogIdRs.next();
				int newLogId =pLogIdRs.getInt(1)+1;
				
				pStm = conn.prepareStatement(sqlInsertProductLogs.toString());
				
				for(ProductLogsTable pt:ProductLogsTable.values()) {
					int idx=pt.getIdx();
					switch (pt) {
						case PRODUCT_LOG_ID -> pStm.setLong(idx, newLogId);
						case PRODUCT_ID -> pStm.setInt(idx,productId);
						case UPDATED_AT -> pStm.setTimestamp(idx,updatedAt);
						case NAME -> pStm.setString(idx, product.getName());
						case CATEGORY_ID -> pStm.setInt(idx, cateId);
						case MANUFACTURER_ID -> pStm.setInt(idx, manuId);
						case LIST_PRICE -> pStm.setBigDecimal(idx, product.getListPrice());
						case RELEASE_DATE -> pStm.setTimestamp(idx, releaseDate);
						case CONTENTS -> pStm.setString(idx, product.getContentURL());
						case TAX_CATEGORY_ID -> pStm.setInt(idx,product.getTaxCategory().getId());
						case STOCK ->{ 
							if(product.getStock() == null) {
								pStm.setNull(idx, java.sql.Types.INTEGER);
							}
							else {
								pStm.setInt(idx, product.getStock());
							}
						}
						case IS_PUBLIC -> pStm.setBoolean(idx, product.getIsPublic());
					}
				}
				
				result += pStm.executeUpdate();	
			}
			
			if(result == 2) { 
				conn.commit();
				System.out.println("ProductDAO updateProduct comit result:"+result);
				System.out.println("ProductDAO updateProduct comit productId:"+product.getId());
			}
			else {
				conn.rollback();
				System.out.println("ProductDAO updateProduct rollback result:"+result);
				System.out.println("ProductDAO updateProduct rollback productId:"+product.getId());
			}
			
			return result;
		}
		catch(SQLException e){
			e.printStackTrace();
			return -1;
		}
	}
	
	//stock を 減算 stock 0より多い時 
	public int subStock(String productId, int quanity) {
		int result = -1;
		try(Connection conn = DriverManager.getConnection(Url,User,Pass)){
			String colStock=ProductsTable.STOCK.column();
			String colProductId=ProductsTable.STOCK.column();
			
			StringBuilder sql =new StringBuilder("UPDATE products SET ");
			sql.append(colStock+" = "+colStock+"- ? WHERE");
			sql.append(colProductId).append("= ? AND ");
			sql.append(colStock+" > 0");
			
			PreparedStatement pStm = conn.prepareStatement(sql.toString());
			
			pStm.setInt(ProductsTable.STOCK.getIdx(), quanity);
			pStm.setString(ProductsTable.STOCK.getIdx(), productId);
			
			result = pStm.executeUpdate();
			System.out.print(sql.toString());
		}
		catch(SQLException e){
			e.printStackTrace();
			return -1;
		}
		return result;
	}

	public String getUser() {
		return User;
	}
	public String getPass() {
		return Pass;
	}
	public String getUrl() {
		return Url;
	}
}
