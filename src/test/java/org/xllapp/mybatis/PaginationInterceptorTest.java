package org.xllapp.mybatis;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;
import org.xllapp.mybatis.Page;
import org.xllapp.mybatis.demo.dao.DemoDao;
import org.xllapp.mybatis.demo.entity.Demo;
import org.xllapp.mybatis.support.ICityXMLConfigBuilder;

/**
 *
 *
 * @Copyright: Copyright (c) 2014 FFCS All Rights Reserved 
 * @Company: 北京福富软件有限公司 
 * @author 陈作朋 Mar 23, 2014
 * @version 1.00.00
 * @history:
 * 
 */
public class PaginationInterceptorTest extends MyBatisTestSupport{

	@Test
	public void test1() throws IOException{
		SqlSession session = getSqlSessionFactory().openSession();
		try {
		  DemoDao dao = session.getMapper(DemoDao.class);
		  Map<String, Object> params = new HashMap<String, Object>();
		  Page<Demo> page = new Page<Demo>(1, 1);
//	      params.put("page", page);
//		  params.put("name", "aaa");
//		  dao.query(params);
/*	      Demo demo=new Demo();
	      demo.setName("aaa");*/
		  dao.query2(params,page);
		  System.out.println(page);
//		  dao.query(params);
		} finally {
		  session.close();
		}
	}
	
}
