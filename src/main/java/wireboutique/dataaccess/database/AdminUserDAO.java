package wireboutique.dataaccess.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import wireboutique.beans.AdminUserBean;
import wireboutique.beans.PasswordBean;
import wireboutique.dataaccess.database.tables.AdminUserPermissionsTable;
import wireboutique.dataaccess.database.tables.AdminUserRolesTable;
import wireboutique.dataaccess.database.tables.AdminUsersTable;


public class AdminUserDAO extends DatabaseDAO {
	/*get~~methodで使うResult定義 Table定義があれば要らないかも
	public enum ResultCol {
		USER_ID(1,AdminUsersTable.ADMIN_USER_ID.column()),
		PASSWORD(2,AdminUsersTable.PASSWORD.column()),
		ROLE(3,AdminUserRolesTable.ROLE_NAME.column());
		private final int Idx;
		private final String Name;
		
		private ResultCol(int Idx,String name) {
			this.Idx=Idx;
			this.Name=name;
		}
		
		public int getIdx() {
			return this.Idx;
		}
		public String getName() {
			return this.Name;
		}
		
		public static ResultTable getColumn(String columnName) {
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
	*/
	
	private final String AdminUsersTableName = AdminUsersTable.ROLE_ID.tableName();
	private final String RolesTableName = AdminUserRolesTable.ROLE_ID.tableName();
	private final String permissionsTableName = AdminUserPermissionsTable.ROLE_ID.tableName();
	
	public AdminUserBean getAdminUser(String userId) {
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append("u.").append(AdminUsersTable.ADMIN_USER_ID.column()).append(",")
			.append("u.").append(AdminUsersTable.PASSWORD.column()).append(",")
			.append("r.").append(AdminUserRolesTable.ROLE_ID.column()).append(",")
			.append("r.").append(AdminUserRolesTable.ROLE_NAME.column())
			.append(" FROM ")
			.append(AdminUsersTableName).append(" u")
			.append(" INNER JOIN ")
			.append(RolesTableName).append(" r")
			.append(" ON ")
			.append("u.").append(AdminUsersTable.ROLE_ID.column()).append(" = ").append("r.").append(AdminUserRolesTable.ROLE_ID)
			.append(" WHERE ")
			.append("u.").append(AdminUsersTable.ADMIN_USER_ID).append(" = ").append("'").append(userId).append("' ");
		
		try(Connection conn = DriverManager.getConnection(Url,User,Pass)){
			PreparedStatement pStm = conn.prepareStatement(sql.toString());
			
			//System.out.println("AdminUserDAO getAdminUser(userId) sql:"+sql);
			
			ResultSet rs = pStm.executeQuery();
			
			String uId="",roleId="",stPassword="",role="";
			
			if(rs.next()) {
				uId = rs.getString(AdminUsersTable.ADMIN_USER_ID.column());
				roleId = rs.getString(AdminUsersTable.ROLE_ID.column());
				stPassword = rs.getString(AdminUsersTable.PASSWORD.column());
				role = rs.getString(AdminUserRolesTable.ROLE_NAME.column());
			}
			
			PasswordBean password = new PasswordBean();
			password.parse(stPassword);
			return new AdminUserBean(uId, password,roleId, role);

		}
		catch (SQLException e) {
			e.printStackTrace();
			
			return null;
		}
	}
	
	public ArrayList<AdminUserBean> getAdminUsers(String roleId) {
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append("u.").append(AdminUsersTable.ADMIN_USER_ID.column()).append(",")
			.append("u.").append(AdminUsersTable.PASSWORD.column()).append(",")
			.append("r.").append(AdminUserRolesTable.ROLE_ID.column()).append(",")
			.append("r.").append(AdminUserRolesTable.ROLE_NAME.column())
			.append(" FROM ")
			.append(AdminUsersTableName).append(" u")
			.append(" INNER JOIN ")
			.append(RolesTableName).append(" r")
			.append(" ON ")
			.append("u.").append(AdminUsersTable.ROLE_ID.column()).append(" = ").append("r.").append(AdminUserRolesTable.ROLE_ID)
			.append(" WHERE ")
			.append("u.").append(AdminUsersTable.ROLE_ID).append(" = ").append("'").append(roleId).append("' ");
		
		try(Connection conn = DriverManager.getConnection(Url,User,Pass)){
			PreparedStatement pStm = conn.prepareStatement(sql.toString());
			
			//System.out.println("AdminUserDAO getAdminUser(userId) sql:"+sql);
			
			ResultSet rs = pStm.executeQuery();
			
			String uId="",getRoleId="",stPassword="",role="";
			
			ArrayList<AdminUserBean> users = new ArrayList<AdminUserBean>();
			
			while(rs.next()) {
				uId = rs.getString(AdminUsersTable.ADMIN_USER_ID.column());
				getRoleId = rs.getString(AdminUsersTable.ROLE_ID.column());
				stPassword = rs.getString(AdminUsersTable.PASSWORD.column());
				role = rs.getString(AdminUserRolesTable.ROLE_NAME.column());
			
				PasswordBean password = new PasswordBean();
				password.parse(stPassword);
				
				users.add( new AdminUserBean(uId, password,getRoleId, role));
			}

			return users;

		}
		catch (SQLException e) {
			e.printStackTrace();
			
			return null;
		}
	}
	
	public int setAdminUser(AdminUserBean user) {
		int result = -1;
		
		try(Connection conn = DriverManager.getConnection(Url,User,Pass)){
			StringBuilder sql =new StringBuilder("INSERT INTO ")
					.append(AdminUsersTableName)
					.append(" (");
			
			for(AdminUsersTable column:AdminUsersTable.values()) {
				sql.append(column.toString());
				if(column.getIdx()!=AdminUsersTable.values().length)
					sql.append(", ");
			}
			
			sql.append(") VALUES (?");
			for(int i=0;i<AdminUsersTable.values().length-1;i++)
				sql.append(",?");
			sql.append(")");
			
			PreparedStatement pStm = conn.prepareStatement(sql.toString());
			
			String roleId = user.getRoleId();
			
			pStm.setString(AdminUsersTable.ADMIN_USER_ID.getIdx(), user.getId());
			pStm.setString(AdminUsersTable.PASSWORD.getIdx(), user.getPassword().toString());
			pStm.setString(AdminUsersTable.ROLE_ID.getIdx(), roleId);
			
			
			//System.out.println("AdminUserDAO setAdminUser sql: " + sql.toString());
			result = pStm.executeUpdate();

			
			return result;
		}
		catch(SQLException e){
			e.printStackTrace();
			return -1;
		}
	}
	
	public int updateAdminUser(AdminUserBean user){
		StringBuilder sql =new StringBuilder("UPDATE ")
				.append(AdminUsersTableName)
				.append(" SET ")
				.append(AdminUsersTable.PASSWORD).append(" = ?,")
				.append(AdminUsersTable.ROLE_ID).append(" = ?")
				.append(" WHERE ")
				.append(AdminUsersTable.ADMIN_USER_ID).append(" = '").append(user.getId()).append("'");
	
		try(Connection conn = DriverManager.getConnection(Url,User,Pass)){
			String roleId="";
			
			roleId = user.getRoleId();
			
			PreparedStatement pStm = conn.prepareStatement(sql.toString());
			
			
			pStm.setString(1, user.getPassword().toString());
			pStm.setString(2, roleId);
			
			int result = pStm.executeUpdate();
			
			return result;
		}
		catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	public String getRoleId(String roleName) {
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(AdminUserRolesTable.ROLE_ID.column()).append(",")
			.append(AdminUserRolesTable.ROLE_NAME)
			.append(" FROM ")
			.append(RolesTableName)
			.append(" WHERE ")
			.append(AdminUserRolesTable.ROLE_NAME).append(" = ").append(" '").append(roleName).append("' ");
		
		try(Connection conn = DriverManager.getConnection(Url,User,Pass)){
			PreparedStatement pStm = conn.prepareStatement(sql.toString());
			ResultSet rs = pStm.executeQuery();
			
			String roleId="";
			
			if(rs.next()) {
				roleId = rs.getString(AdminUserRolesTable.ROLE_ID.column());
			}
			
			return roleId;
		}
		catch (SQLException e) {
			e.printStackTrace();
			
			return null;
		}
	}
	
	public String getRoleName(String roleId) {
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(AdminUserRolesTable.ROLE_NAME)
			.append(" FROM ")
			.append(RolesTableName)
			.append(" WHERE ")
			.append(AdminUserRolesTable.ROLE_ID).append(" = ").append(" '").append(roleId).append("' ");
		
		try(Connection conn = DriverManager.getConnection(Url,User,Pass)){
			PreparedStatement pStm = conn.prepareStatement(sql.toString());
			ResultSet rs = pStm.executeQuery();
			
			String roleName="";
			
			rs.next();
			
			roleName = rs.getString(AdminUserRolesTable.ROLE_NAME.column());
			
			return roleName;
		}
		catch (SQLException e) {
			e.printStackTrace();
			
			return null;
		}
	}
	
	//return map<role_id, role_name>
	public HashMap<String,String> getRoles() {
		StringBuilder sql = new StringBuilder("SELECT ");
		
		sql.append(AdminUserRolesTable.ROLE_ID.column()).append(",")
			.append(AdminUserRolesTable.ROLE_NAME.column())
			.append(" FROM ")
			.append(RolesTableName);
		
		try(Connection conn = DriverManager.getConnection(Url,User,Pass)){
			PreparedStatement pStm = conn.prepareStatement(sql.toString());
			ResultSet rs = pStm.executeQuery();
			
			HashMap<String, String> roles = new HashMap<String, String>();
			
			while(rs.next()){
				String id = rs.getString(AdminUserRolesTable.ROLE_ID.column());
				String name = rs.getString(AdminUserRolesTable.ROLE_NAME.column());;

				roles.put(id, name);
			}

			return roles;
		}
		catch (SQLException e) {
			e.printStackTrace();		
			return null;
		}
	}
		
	public int setRole(String roleName, Map<String, Boolean> permissions){
		try(Connection conn = DriverManager.getConnection(Url,User,Pass)){
			
			
			int result=-1;
			
			conn.setAutoCommit(false);
			
			String latestId=null, newId=null;
			//get latestId
			{
				StringBuilder sql = new StringBuilder("SELECT ");
				
				sql.append(AdminUserRolesTable.ROLE_ID.column())
					.append(" FROM ")
					.append(RolesTableName)
					.append(" ORDER BY ")
					.append(AdminUserRolesTable.ROLE_ID.column()).append(" DESC")
					.append(" LIMIT 1");
					
				PreparedStatement pStm = conn.prepareStatement(sql.toString());
				ResultSet rs = pStm.executeQuery();
				
				if(rs.next()) {
					latestId = rs.getString(1);
				
					//newIdはIDの最大値のインクリメント
					newId = String.valueOf(Integer.valueOf(latestId)+1);
				}
				else {
					newId = String.valueOf(Integer.valueOf(0));
				}
			}
			
			//insert role table
			{
				StringBuilder sql = new StringBuilder("INSERT INTO ");
				
				sql.append(AdminUserRolesTable.ROLE_ID.tableName())
					.append(" (")
					.append(AdminUserRolesTable.ROLE_ID.column()).append(",")
					.append(AdminUserRolesTable.ROLE_NAME)
					.append(")")
					.append(" VALUES ( ?,?)");
				
				PreparedStatement pStm = conn.prepareStatement(sql.toString());
				pStm.setString(1, newId);
				pStm.setString(2, roleName);
				result = pStm.executeUpdate();
			}
			
			//insert permission table
			if(result == 1){
				StringBuilder sql = new StringBuilder("INSERT INTO ");
				
				sql.append(AdminUserPermissionsTable.ROLE_ID.tableName())
					.append(" (");
					
					for(AdminUserPermissionsTable col :AdminUserPermissionsTable.values()) {
						sql.append(col.column());
						if(col.getIdx() != AdminUserPermissionsTable.values().length)
							sql.append(", ");
					}
					sql.append(")")
					.append(" VALUES (")
					.append("?");
					for(int i=0;i<AdminUserPermissionsTable.values().length-1;i++)
						sql.append(",?");
					sql.append(")");
				
				PreparedStatement pStm = conn.prepareStatement(sql.toString());
				
				for(AdminUserPermissionsTable col :AdminUserPermissionsTable.values()) {
					if(col == AdminUserPermissionsTable.ROLE_ID)
						pStm.setString(col.getIdx(), newId);
					else
						pStm.setBoolean(col.getIdx(), permissions.get(col.column()));
				}
				
				result += pStm.executeUpdate();
			}
			else {
				conn.rollback();
				System.out.println("AdminUserDAO setRole faild set role_table");
			}
			
			//commit
			//admin_user_role と admin_user_permissions が1行ずつ更新出来ていればcommit
			if(result == 2) {
				conn.commit();
				System.out.println("AdminUserDAO setRole commit : "+result);
			}
			else {
				conn.rollback();
				System.out.println("AdminUserDAO setRole rollback : "+result);
				result = 0;
			}

			return result;
		}
		catch (SQLException e) {
			e.printStackTrace();		
			return -1;
		}
	}
	
	public int updateRoleName(String roleId,String newName) {
		StringBuilder sql =new StringBuilder("UPDATE ")
				.append(RolesTableName)
				.append(" SET ")
				.append(AdminUserRolesTable.ROLE_NAME.column()).append(" = ?")
				.append(" WHERE ")
				.append(AdminUserRolesTable.ROLE_ID.column()).append(" = '").append(roleId).append("'");
	
		try(Connection conn = DriverManager.getConnection(Url,User,Pass)){
			PreparedStatement pStm = conn.prepareStatement(sql.toString());
			
			pStm.setString(1, newName);
			
			int result = pStm.executeUpdate();
			
			return result;
		}
		catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	public HashMap<String, Boolean> getPermissions(AdminUserBean user){
		StringBuilder sql = new StringBuilder("SELECT ");
		
		for(AdminUserPermissionsTable col :AdminUserPermissionsTable.values()) {
			sql.append(col.column()).append(",");
		}
		sql.deleteCharAt(sql.length()-1);
		
		sql.append(" FROM ")
			.append(permissionsTableName)
			
			.append(" WHERE ")
			.append(AdminUserPermissionsTable.ROLE_ID.column())
			.append(" =")
			.append(" '").append(user.getRoleId()).append("' ");

		try(Connection conn = DriverManager.getConnection(Url,User,Pass)){
			PreparedStatement pStm = conn.prepareStatement(sql.toString());
			
			//System.out.println("AdminUserDAO getPermissions(user) sql:"+sql);
			
			ResultSet rs = pStm.executeQuery();

			HashMap<String, Boolean> permissions= new HashMap<String, Boolean>();
			if(rs.next()) {
				//role_id以外のresult columnをHashMapへ格納
				for(AdminUserPermissionsTable col :AdminUserPermissionsTable.values()) {
					if(col != AdminUserPermissionsTable.ROLE_ID) {
						//table定義クラスのcolumn名をKey(Permission) に
						permissions.put(col.column(), rs.getBoolean(col.column()));
					}
				}
			}
			else {
				System.out.println("AdminUserDAO getPermissions: resulSet null /user: "+user.getId()+" role: "+user.getRoleName());
			}
			
			return permissions;
		}
		catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public HashMap<String, Boolean> getPermissions(String roleId) {
		StringBuilder sql = new StringBuilder("SELECT ");
		
		sql.append("* FROM ")
			.append(AdminUserPermissionsTable.ROLE_ID.tableName())
			.append(" WHERE ")
			.append(AdminUserPermissionsTable.ROLE_ID)
			.append(" = ")
			.append(roleId);
		
		try(Connection conn = DriverManager.getConnection(Url,User,Pass)){
			PreparedStatement pStm = conn.prepareStatement(sql.toString());
			ResultSet rs = pStm.executeQuery();
			
			HashMap<String, Boolean> permissions= new HashMap<String, Boolean>();
			if(rs.next()) {	
				//role_id以外のresult columnをHashMapへ格納
				for(AdminUserPermissionsTable col :AdminUserPermissionsTable.values()) {
					if(col != AdminUserPermissionsTable.ROLE_ID) {
						//table定義クラスのcolumn名をKey(Permission) に
						permissions.put(col.column(), rs.getBoolean(col.column()));
					}
				}
			}
			else {
				System.out.println("AdminUserDAO getPermissions: resulSet null /roleId: "+roleId);
			}
			
			
			return permissions;
		}
		catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public int updatePermission(String roleId, Map<String, Boolean> permissions){
		StringBuilder sql = new StringBuilder("UPDATE ");
		
		sql.append(permissionsTableName)
			.append(" SET ");
		
			for(AdminUserPermissionsTable col :AdminUserPermissionsTable.values()) {
				sql.append(col.column()).append(" = ?");
				if(col.getIdx() != AdminUserPermissionsTable.values().length)
					sql.append(",");
			}
			
		sql.append(" WHERE ")
			.append(AdminUserPermissionsTable.ROLE_ID.column())
			.append(" = '").append(roleId).append("'");
	
		
		try(Connection conn = DriverManager.getConnection(Url,User,Pass)){
			PreparedStatement pStm = conn.prepareStatement(sql.toString());

			for(AdminUserPermissionsTable col :AdminUserPermissionsTable.values()) {
				if(col == AdminUserPermissionsTable.ROLE_ID)
					pStm.setString(col.getIdx(), roleId);
				else
					pStm.setBoolean(col.getIdx(), permissions.get(col.column()));
			}
			
			//System.out.println("AdminUserDAO updataPermission sql: "+pStm);
			
			int result = pStm.executeUpdate();

			return result;
		}
		catch (SQLException e) {
			e.printStackTrace();		
			return -1;
		}
	}
}
