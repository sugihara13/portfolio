package wireboutique.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;


//使わなくていいかも(DAO個別で処理書いたほうがいいかも)
//DBの接続情報はInterfaceなり定数なりで
//継承するならtry-with-resouceでgetConnectionをautocloseできるようにすれば か
public class ConnectDatabase {
	private String User,Pass;
	private String Url;
	
	//use default 
	public ConnectDatabase() {
			//set default info
			User="sqlTester";
			Pass="testerpass";
			Url="jdbc:mysql://localhost/wireboutique";
	}
	
	public ConnectDatabase(String user, String password, String url, boolean isDefault) {
		if(isDefault) {
			//set default info
			User="";
			Pass="";
			Url="";
		}
		else {
			User=user;
			Pass=password;
			Url=url;
		}
	}
	
	
	//Object型でtableを取得 別でキャスト出来ればいいかも
	public ArrayList<ArrayList<Object>> getTable(String sql) {
		ArrayList<ArrayList<Object>> table = new ArrayList<ArrayList<Object>>();
		
		try(Connection conn = DriverManager.getConnection(Url,User,Pass)){
			PreparedStatement pStm = conn.prepareStatement(sql);
			ResultSet rs = pStm.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			
			int colomnNumber = rsmd.getColumnCount();
			
			while (rs.next()) {
				ArrayList<Object> record = new ArrayList<Object>();
				for(int i = 1; i <= colomnNumber; i++) {
					record.add(rs.getObject(i));
				}

				table.add(record);
			}
		}
		catch(SQLException e){
			e.printStackTrace();
			table = new ArrayList<ArrayList<Object>>();
			return table;
		}
		
		return table;
	}
	
	//sqlのテーブルのレコードをT型にしてListでテーブルを返す
	//validator レコードをmapperで処理出来るかをチェックする関数
	//mapper レコードをlistで受け取りT型で返す関数
	//validator/mapperで想定される列順のテーブルが必要　
	public <T> ArrayList<T> getTable(String sql,Function<ResultSetMetaData,Boolean> validator,Function<ArrayList<Object>, T> mapper){
		ArrayList<T> table = new ArrayList<T>();
		
		try(Connection conn = DriverManager.getConnection(Url,User,Pass)){
			PreparedStatement pStm = conn.prepareStatement(sql);
			ResultSet rs = pStm.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			
			if(validator.apply(rsmd)) {
				while (rs.next()) {
					ArrayList<Object> record = new ArrayList<Object>();
					
					for(int i=0	;i	<rsmd.getColumnCount();i++) {
						record.add(rs.getObject(i+1));
					}
					table.add(mapper.apply(record));
				}
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
			return null;
		}
		
		return table;
	}
	
	//sqlをexecuteQueryしてテーブルのレコードをT型にしてListで返す
	//mapBuilder レコードをT型にするmapper関数を作成する関数
	//mapper レコードをlistで受け取りT型で返す関数 mapBuilderによって作成される
	//mapBuilderに渡されるResultSetMetaDataからレコードのマッピングルールを決めてmapperを定義
	 public <T> ArrayList<T> getTable(String sql,Function<ResultSetMetaData,Function<ArrayList<Object>, T>> mapBuilder){
		ArrayList<T> table = new ArrayList<T>();
		
		try(Connection conn = DriverManager.getConnection(Url,User,Pass)){
			PreparedStatement pStm = conn.prepareStatement(sql);
			ResultSet rs = pStm.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			
			Function<ArrayList<Object>, T> mapper = mapBuilder.apply(rsmd);
			
			if(mapper!=null) {
				while (rs.next()) {
					ArrayList<Object> record = new ArrayList<Object>();
					
					for(int i=1	;i	<=rsmd.getColumnCount();i++) {
						record.add(rs.getObject(i));
					}
					table.add(mapper.apply(record));
				}
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
			return null;
		}
		
		return table;
	}
	
	public int setTable(String sql) {
		int result;
		try(Connection conn = DriverManager.getConnection(Url,User,Pass)){
			PreparedStatement pStm = conn.prepareStatement(sql);
			result = pStm.executeUpdate();
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
