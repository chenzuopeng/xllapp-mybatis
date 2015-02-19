package org.xllapp.mybatis;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.xllapp.mybatis.MybatisContext;

/**
 * 此类用于加载
 *
 * @author dylan.chen Jan 14, 2015
 * 
 */
@Lazy(false)
@Component
public class MybatisContextLoader implements InitializingBean{
	
    private String environment;

    @Value("${spring.profiles.active:${spring.profiles.default}}")
	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		MybatisContext.setEnvironment(this.environment);
	}
	
}
