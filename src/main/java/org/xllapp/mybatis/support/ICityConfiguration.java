package org.xllapp.mybatis.support;

import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;

/**
 * 使用自定义的ICityMapperRegistry类.扩展自Mybatis3.1.1版本的Configuration类.
 * 
 * @author dylan.chen Mar 23, 2014
 * 
 */
public class ICityConfiguration extends Configuration {

	protected MapperRegistry mapperRegistry = new ICityMapperRegistry(this);

	public <T> void addMapper(Class<T> type) {
		mapperRegistry.addMapper(type);
	}

	public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
		return mapperRegistry.getMapper(type, sqlSession);
	}

	public boolean hasMapper(Class<?> type) {
		return mapperRegistry.hasMapper(type);
	}

}
