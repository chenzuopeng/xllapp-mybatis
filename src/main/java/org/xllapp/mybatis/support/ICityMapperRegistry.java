package org.xllapp.mybatis.support;

import org.apache.ibatis.binding.BindingException;
import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;

/**
 * 使用自定义的ICityMapperProxy类.基于Mybatis3.1.1版本的XMLConfigBuilder类进行修改.
 * 
 * @author dylan.chen Mar 23, 2014
 * 
 */
public class ICityMapperRegistry extends MapperRegistry {

	public ICityMapperRegistry(Configuration config) {
		super(config);
	}
	
	public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
	    if (!hasMapper(type))
	      throw new BindingException("Type " + type + " is not known to the MapperRegistry.");
	    try {
	      return ICityMapperProxy.newMapperProxy(type, sqlSession);
	    } catch (Exception e) {
	      throw new BindingException("Error getting mapper instance. Cause: " + e, e);
	    }
	  }

}
