package wireboutique.dataaccess.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

import wireboutique.beans.PasswordBean;
import wireboutique.beans.UserBean;
import wireboutique.dataaccess.database.tables.UsersTable;

public class UserDAO extends DatabaseDAO {
	public enum Column {
		USER_ID(1,UsersTable.USER_ID.column()),
		PASSWORD(2,UsersTable.PASSWORD.column());

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
			if(USER_ID.Name.equals(columnName)) {
				return USER_ID;
			}
			else if(PASSWORD.Name.equals(columnName)) {
				return PASSWORD;
			}
			else {
				return null;
			}
		}
	}
	
	public ArrayList<UserBean>  getUsers(String key,int recordcount,boolean order) {
		//build sql for preparedstatement----
		String sortkey = Column.USER_ID.getName();
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
			sql.append("u."+Column.USER_ID.getName()).append(",u."+Column.PASSWORD.getName());
			sql.append(" FROM users u");
			sql.append(" ORDER BY u.");
			sql.append(sortkey).append(sqlorder).append("LIMIT ").append(sqlcount);
			//-------------------------------------------
			
			ArrayList<UserBean> userList = new ArrayList<UserBean>();
			
			try(Connection conn = DriverManager.getConnection(Url,User,Pass)){
				PreparedStatement pStm = conn.prepareStatement(sql.toString());
				ResultSet rs = pStm.executeQuery();
				ResultSetMetaData rsmd = rs.getMetaData();
			
				while (rs.next()) {
					String id="",stPassword="";
					
					for(int i=1;i<=rsmd.getColumnCount();i++) {
						Column column = Column.getColumn(rsmd.getColumnName(i));
						
						switch(column) {
							case USER_ID:	id = rs.getString(i); break;
							case PASSWORD:	stPassword = rs.getString(i)	; break;
						}
					}
					
					PasswordBean password = new PasswordBean();
					password.parse(stPassword);
					
					UserBean user = new UserBean(id,password);
					userList.add(user);
				}
			}
			catch (SQLException e) {
				e.printStackTrace();
				userList = new ArrayList<UserBean>();
				return null;
			}
			
			return userList;
	}
	
	public UserBean getUser(String userId) {
		//build sql for preparedstatement
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append("`"+Column.USER_ID.getName()+"`").append(", `"+Column.PASSWORD.getName());
		sql.append("` FROM users");
		sql.append(" WHERE "+Column.USER_ID.getName()+" = '").append(userId+"' ;");
				
		UserBean user = null;
		
		//System.out.println(sql);
		
		try(Connection conn = DriverManager.getConnection(Url,User,Pass)){
			PreparedStatement pStm = conn.prepareStatement(sql.toString());
			ResultSet rs = pStm.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			
			//product param
			String id="",stPassword="";
			//param と同じテーブル名から getObject
			rs.next();
			
			for(int i=1;i<=rsmd.getColumnCount();i++) {
				//Column column = Column.valueOf(rsmd.getColumnName(i));
				
				//このDAOのTable定数にresultSetのcolumnNameをマッピング
				Column column = Column.getColumn(rsmd.getColumnName(i));
				switch(column) {
					//case = Column Name
					case USER_ID:	id = rs.getString(i); break;
					case PASSWORD: stPassword = rs.getString(i); break;
				}
			}
			
			PasswordBean password = new PasswordBean();
			password.parse(stPassword);
			
			user = new UserBean(id,password);

		}
		catch(SQLException e){
			e.printStackTrace();
			user = new UserBean();
			return user;
		}
		
		return user;
	}
	
	public int setUser(UserBean user) {
		int result = -1;
		try(Connection conn = DriverManager.getConnection(Url,User,Pass)){
			StringBuilder sql =new StringBuilder("INSERT INTO users (");
			for(UsersTable column:UsersTable.values()) {
				sql.append(column.toString());
				if(column.getIdx()!=UsersTable.values().length)
					sql.append(", ");
			}
			sql.append(") VALUES (?");
			for(int i=0;i<UsersTable.values().length-1;i++)
				sql.append(",?");
			sql.append(")");
			
			PreparedStatement pStm = conn.prepareStatement(sql.toString());
			
			pStm.setString(UsersTable.USER_ID.getIdx(), user.getId());
			pStm.setString(UsersTable.PASSWORD.getIdx(), user.getPassword().toString());
			
			result = pStm.executeUpdate();
			System.out.println(sql.toString());
		}
		catch(SQLException e){
			e.printStackTrace();
			return -1;
		}
		return result;
	}
}
