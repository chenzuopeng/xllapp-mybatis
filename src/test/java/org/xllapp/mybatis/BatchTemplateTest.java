package org.xllapp.mybatis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.junit.Test;
import org.xllapp.mybatis.BatchTemplate;
import org.xllapp.mybatis.demo.dao.DemoDao;
import org.xllapp.mybatis.demo.entity.Demo;

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
public class BatchTemplateTest extends MyBatisTestSupport {
	
	@Test
	public void test1() throws IOException{
		
		List<Demo> list = new ArrayList<Demo>();
		
		for (int i = 0; i < 21; i++) {
			Demo demo=new Demo();
			demo.setName(i+"");
			demo.setPassword(i+"");
			list.add(demo);
		}
		
		SqlSession session = getSqlSessionFactory().openSession();
		final DemoDao dao = session.getMapper(DemoDao.class);
		
		try {
			BatchTemplate.execute(list,10, new BatchTemplate.BatchCallback<Demo>() {
				
				@Override
				public void doBatch(int i, List<Demo> batch) {
					dao.batchInsert(batch);
				}
			});
		} finally {
			session.commit();
			session.close();
		}
		
	}

	/**
	 * 其中一批执行错误
	 */
	@Test
	public void test2() throws IOException{
		
		List<Demo> list = new ArrayList<Demo>();
		
		for (int i = 0; i < 21; i++) {
			Demo demo=new Demo();
			if(i ==18){
				demo.setName("17");
			}else{
				demo.setName(i+"");
			}
			
			demo.setPassword(i+"");
			list.add(demo);
		}
		
		SqlSession session = getSqlSessionFactory().openSession();
		final DemoDao dao = session.getMapper(DemoDao.class);
		
		try {
			BatchTemplate.execute(list,10, new BatchTemplate.BatchCallback<Demo>() {
				
				@Override
				public void doBatch(int i, List<Demo> batch) {
					dao.batchInsert(batch);
				}
			});
		} finally {
			session.commit();
			session.close();
		}
		
	}
	
	/**
	 * 默认批大小
	 */
	@Test
	public void test3() throws IOException{
		
		List<Demo> list = new ArrayList<Demo>();
		
		for (int i = 0; i < 121; i++) {
			Demo demo=new Demo();
			demo.setName(i+"a");
			demo.setPassword(i+"a");
			list.add(demo);
		}
		
		SqlSession session = getSqlSessionFactory().openSession();
		final DemoDao dao = session.getMapper(DemoDao.class);
		
		try {
			BatchTemplate.execute(list, new BatchTemplate.BatchCallback<Demo>() {
				
				@Override
				public void doBatch(int i, List<Demo> batch) {
					dao.batchInsert(batch);
				}
			});
		} finally {
			session.commit();
			session.close();
		}
		
	}
	
}
