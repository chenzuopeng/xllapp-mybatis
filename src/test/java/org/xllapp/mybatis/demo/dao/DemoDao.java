package org.xllapp.mybatis.demo.dao;

import java.util.List;
import java.util.Map;

import org.xllapp.mybatis.MyBatisRepository;
import org.xllapp.mybatis.Page;
import org.xllapp.mybatis.demo.entity.Demo;

/**
 *
 *
 * @Copyright: Copyright (c) 2014 FFCS All Rights Reserved 
 * @Company: 北京福富软件有限公司 
 * @author 陈作朋 Mar 17, 2014
 * @version 1.00.00
 * @history:
 * 
 */
@MyBatisRepository
public interface DemoDao {

	Demo get(Long id);

	void insert(Demo t);

	void update(Demo t);

	void delete(Long id);

	void deletes(Long[] ids);

	List<Demo> query(Map<String, Object> parameters);
	
	List<Demo> query2(Map<String, Object> parameters,Page<?> page);
	
	void batchInsert(List<Demo> list);
	
}
