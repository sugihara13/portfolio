package wireboutique.dataaccess.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import wireboutique.dataaccess.database.tables.CategoriesTable;

public class CategoryDAO extends DatabaseDAO{
	//return category name
	public String getName(int categoryId) {
		StringBuilder sql = new StringBuilder("SELECT ")
					.append(CategoriesTable.CATEGORY.column());
			
		sql.append(" FROM ")
			.append(CategoriesTable.CATEGORY_ID.tableName())
			.append(" WHERE ")
			.append(CategoriesTable.CATEGORY_ID.column()).append(" = ").append(categoryId);
		
		try(Connection conn = DriverManager.getConnection(Url,User,Pass)){
			PreparedStatement pStm = conn.prepareStatement(sql.toString());
			ResultSet rs = pStm.executeQuery();
				
				
			if(rs.next()) {	
				String name = rs.getString(1);
				
				return name;
			}
				
			return null;
		}
		catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	//Id, Category
	public HashMap<Integer, String> getCategories() {
		StringBuilder sql = new StringBuilder("SELECT ");
		
		sql.append("* FROM ")
			.append(CategoriesTable.CATEGORY_ID.tableName());
		
		try(Connection conn = DriverManager.getConnection(Url,User,Pass)){
			PreparedStatement pStm = conn.prepareStatement(sql.toString());
			ResultSet rs = pStm.executeQuery();
			
			HashMap<Integer, String> categories= new HashMap<Integer, String>();
			while(rs.next()) {	
				Integer id = rs.getInt(CategoriesTable.CATEGORY_ID.getIdx());
				String name = rs.getString(CategoriesTable.CATEGORY.getIdx());
				categories.put(id, name);
			}
			
			return categories;
		}
		catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
}
