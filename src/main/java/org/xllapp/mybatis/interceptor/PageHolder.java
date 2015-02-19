package org.xllapp.mybatis.interceptor;

import org.xllapp.mybatis.Page;

/**
 *
 *
 * @author dylan.chen Mar 23, 2014
 * 
 */
public class PageHolder {

	private static ThreadLocal<Page<?>> page = new ThreadLocal<Page<?>>();
	
	public static Page<?> getPage() {
		return page.get();
	}

	public static void setPage(Page<?> page) {
		PageHolder.page.set(page);
	}
	
	public static void clear() {
		page.remove();
	}
	
}
