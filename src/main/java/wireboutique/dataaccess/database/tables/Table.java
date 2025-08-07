package wireboutique.dataaccess.database.tables;

public interface Table {
	//DB上のtableのcolumn位置Index  Enum.ordinal で定義順序をgetできるがあえてIdxでtable定義示すよ
	public int getIdx();
	//DB上のtableのcolumn名
	public String column();
	
	public String tableName();
}
