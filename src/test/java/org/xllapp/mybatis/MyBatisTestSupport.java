package org.xllapp.mybatis;

import java.io.IOException;
import java.io.InputStream;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.xllapp.mybatis.support.ICityXMLConfigBuilder;

/**
 *
 *
 * @Copyright: Copyright (c) 2014 FFCS All Rights Reserved 
 * @Company: 北京福富软件有限公司 
 * @author 陈作朋 Nov 16, 2014
 * @version 1.00.00
 * @history:
 * 
 */
public abstract class MyBatisTestSupport {
	
	private final static String CONFIG_FILE = "mybatis-config.xml";
	
	public SqlSessionFactory getSqlSessionFactory() throws IOException{
		InputStream inputStream = Resources.getResourceAsStream(CONFIG_FILE);
		ICityXMLConfigBuilder builder=new ICityXMLConfigBuilder(inputStream, null, null);
		return new SqlSessionFactoryBuilder().build(builder.parse());
	}

}
