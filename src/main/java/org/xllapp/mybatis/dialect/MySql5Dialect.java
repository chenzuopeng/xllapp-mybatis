package org.xllapp.mybatis.dialect;


public class MySql5Dialect implements Dialect{
	
	protected static final String SQL_END_DELIMITER = ";";
	

	public String getLimitString(String sql, int offset, int limit) {
		return MySql5PageHepler.getLimitString(sql, offset, limit);
	}

	@Override
	public String getCountString(String querySelect) {
		return MySql5PageHepler.getCountString(querySelect);
	}
}
