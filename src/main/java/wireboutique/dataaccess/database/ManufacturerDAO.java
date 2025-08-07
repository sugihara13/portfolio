package wireboutique.dataaccess.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import wireboutique.dataaccess.database.tables.ManufacturersTable;

public class ManufacturerDAO extends DatabaseDAO {
	
	//return manufacturer name
	public String getName(int manufacturerId) {
		StringBuilder sql = new StringBuilder("SELECT ")
				.append(ManufacturersTable.MANUFACTURER.column());
		
		sql.append(" FROM ")
			.append(ManufacturersTable.MANUFACTURER_ID.tableName())
			.append(" WHERE ")
			.append(ManufacturersTable.MANUFACTURER_ID.column()).append(" = ").append(manufacturerId);
		
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
	
	//Id, Manufacturer
	public HashMap<Integer, String> getManufacturers() {
		StringBuilder sql = new StringBuilder("SELECT ");
		
		sql.append("* FROM ")
			.append(ManufacturersTable.MANUFACTURER_ID.tableName());
		
		try(Connection conn = DriverManager.getConnection(Url,User,Pass)){
			PreparedStatement pStm = conn.prepareStatement(sql.toString());
			ResultSet rs = pStm.executeQuery();
			
			HashMap<Integer, String> manufacturers= new HashMap<Integer, String>();
			while(rs.next()) {	
				Integer id = rs.getInt(ManufacturersTable.MANUFACTURER_ID.getIdx());
				String name = rs.getString(ManufacturersTable.MANUFACTURER.getIdx());
				manufacturers.put(id, name);
			}
			
			return manufacturers;
		}
		catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
}
