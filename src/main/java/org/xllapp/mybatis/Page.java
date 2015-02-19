/**
 * Copyright (c) 2005-2009 springside.org.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * 
 * $Id: Page.java 838 2010-01-06 13:47:36Z calvinxiu $
 */
package org.xllapp.mybatis;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 与具体ORM实现无关的分页参数及查询结果封装. 注意所有序号从1开始.
 * 
 * @param <T>
 *            Page中记录的类型.
 * 
 * @author calvin
 */
public class Page<T> implements Serializable{

	private static final long serialVersionUID = -8678935552059941854L;
	
	// -- 分页参数 --//
	protected int pageNo = 1;
	protected int pageSize = 10;

	// -- 返回结果 --//
	protected List<T> result = new ArrayList<T>();
	protected long totalCount = -1;

	// -- 构造函数 --//
	public Page() {
	}

	public Page(int pageSize) {
		super();
		setPageSize(pageSize);
	}
	
	public Page(int pageNo, int pageSize) {
		super();
		setPageNo(pageNo);
		setPageSize(pageSize);
	}

	// -- 访问查询参数函数 --//
	/**
	 * 获得当前页的页号,序号从1开始,默认为1.
	 */
	public int getPageNo() {
		return this.pageNo;
	}

	/**
	 * 设置当前页的页号,序号从1开始,低于1时自动调整为1.
	 */
	public void setPageNo(final int pageNo) {
		this.pageNo = pageNo;

		if (pageNo < 1) {
			this.pageNo = 1;
		}
	}

	/**
	 * 根据记录在总结果集中的位置,计数出其所在页的页码
	 * */
	public void setPageNoByFirst(int first) {
		setPageNo(first / this.pageSize + 1);
	}

	public Page<T> pageNo(final int thePageNo) {
		setPageNo(thePageNo);
		return this;
	}

	/**
	 * 获得每页的记录数量,默认为1.
	 */
	public int getPageSize() {
		return this.pageSize;
	}

	/**
	 * 设置每页的记录数量,低于1时自动调整为1.
	 */
	public void setPageSize(final int pageSize) {
		this.pageSize = pageSize;

		if (pageSize < 1) {
			this.pageSize = 1;
		}
	}

	public Page<T> pageSize(final int thePageSize) {
		setPageSize(thePageSize);
		return this;
	}

	/**
	 * 根据pageNo和pageSize计算当前页第一条记录在总结果集中的位置,序号从1开始.
	 */
	public long getFirst() {
		return (this.pageNo - 1) * this.pageSize + 1;
	}

	/**
	 * 根据pageNo和pageSize计算当前页最后一条记录在总结果集中的位置,序号从1开始.
	 */
	public long getLast() {
		long last = getFirst() + this.pageSize - 1;
		return last > this.totalCount ? this.totalCount : last;
	}

	// -- 访问查询结果函数 --//

	/**
	 * 取得页内的记录列表.
	 */
	public List<T> getResult() {
		return this.result;
	}

	/**
	 * 设置页内的记录列表.
	 */
	public void setResult(final List<T> result) {
		this.result = result;
	}
	
	/**
	 * 设置页内的记录列表.
	 */
	public void setResult(Page<T> page){
		this.result=page.result;
		this.totalCount=page.totalCount;
	}

	/**
	 * 取得总记录数, 默认值为-1.
	 */
	public long getTotalCount() {
		return this.totalCount;
	}

	/**
	 * 设置总记录数.
	 */
	public void setTotalCount(final long totalCount) {
		this.totalCount = totalCount;
	}

	/**
	 * 根据pageSize与totalCount计算总页数, 默认值为-1.
	 */
	public int getTotalPages() {
		if (this.totalCount < 0) {
			return -1;
		}

		long count = this.totalCount / this.pageSize;
		if (this.totalCount % this.pageSize > 0) {
			count++;
		}
		return new Long(count).intValue();
	}

	/**
	 * 是否还有下一页.
	 */
	public boolean isHasNext() {
		return this.pageNo + 1 <= getTotalPages();
	}

	/**
	 * 取得下页的页号, 序号从1开始. 当前页为尾页时仍返回尾页序号.
	 */
	public int getNextPage() {
		if (isHasNext()) {
			return this.pageNo + 1;
		} else {
			return this.pageNo;
		}
	}

	/**
	 * 是否还有上一页.
	 */
	public boolean isHasPre() {
		return this.pageNo - 1 >= 1;
	}

	/**
	 * 取得上页的页号, 序号从1开始. 当前页为首页时返回首页序号.
	 */
	public int getPrePage() {
		if (isHasPre()) {
			return this.pageNo - 1;
		} else {
			return this.pageNo;
		}
	}
	
	/**
	 * 获取首页的页码
	 */
	public int getFirstPage(){
		return 1;
	}
	
	/**
	 * 获取末页的页码
	 */
	public int getLastPage(){
		return getTotalPages();
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
	}

}
