package wireboutique.dataaccess.database;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import wireboutique.beans.TaxBean;
import wireboutique.dataaccess.database.tables.TaxCategoriesTable;

public class TaxDAO extends DatabaseDAO{
		
	public TaxBean getTaxCategory(int taxId) {
		StringBuilder sql = new StringBuilder("SELECT ");
		
		sql.append("* FROM ")
			.append(TaxCategoriesTable.TAX_CATEGORY_ID.tableName())
			.append(" WHERE ")
			.append(TaxCategoriesTable.TAX_CATEGORY_ID.column()).append(" = ").append(taxId);
		
		try(Connection conn = DriverManager.getConnection(Url,User,Pass)){
			PreparedStatement pStm = conn.prepareStatement(sql.toString());
			ResultSet rs = pStm.executeQuery();
			
			
			if(rs.next()) {	
				Integer id = rs.getInt(TaxCategoriesTable.TAX_CATEGORY_ID.getIdx());
				String name = rs.getString(TaxCategoriesTable.TAX_NAME.getIdx());
				BigDecimal rate = rs.getBigDecimal(TaxCategoriesTable.TAX_RATE.getIdx());
				
				return new TaxBean(id, name, rate);
			}
			
			return null;
		}
		catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public ArrayList<TaxBean> getTaxCategories() {
		StringBuilder sql = new StringBuilder("SELECT ");
			
		sql.append("* FROM ")
			.append(TaxCategoriesTable.TAX_CATEGORY_ID.tableName());
			
		try(Connection conn = DriverManager.getConnection(Url,User,Pass)){
			PreparedStatement pStm = conn.prepareStatement(sql.toString());
			ResultSet rs = pStm.executeQuery();
				
			ArrayList<TaxBean> taxCategories= new ArrayList<TaxBean>();
			while(rs.next()) {	
				Integer id = rs.getInt(TaxCategoriesTable.TAX_CATEGORY_ID.getIdx());
				String name = rs.getString(TaxCategoriesTable.TAX_NAME.getIdx());
				BigDecimal rate = rs.getBigDecimal(TaxCategoriesTable.TAX_RATE.getIdx());
					
				taxCategories.add(new TaxBean(id, name, rate));
			}
				
			return taxCategories;
		}
		catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
}
