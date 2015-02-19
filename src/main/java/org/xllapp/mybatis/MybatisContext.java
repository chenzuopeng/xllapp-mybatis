package org.xllapp.mybatis;
import org.apache.commons.lang3.StringUtils;


/**
 * 
 * 此类包含全局上下文信息.
 *
 * @author dylan.chen Jan 13, 2015
 * 
 */
public class MybatisContext {
	
	private static String environment;
	
	private MybatisContext(){
	}
	
	public static String getEnvironment() {
		return environment;
	}

	public static void setEnvironment(String input) {
		environment = input;
	}
	
	public static boolean isActivatedEnvironment(String input){
		return StringUtils.isNotBlank(input) 
				&& StringUtils.equals(environment, input);
	}
	
}
