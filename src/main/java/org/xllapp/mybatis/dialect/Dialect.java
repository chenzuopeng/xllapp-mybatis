package org.xllapp.mybatis.dialect;

public interface Dialect {

	public static enum Type{
		MYSQL,
		ORACLE
	}
	
	public String getLimitString(String sql, int offset, int limit);
	
	public String getCountString(String querySelect);
	
}
