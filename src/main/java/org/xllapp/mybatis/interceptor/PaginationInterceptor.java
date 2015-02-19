package org.xllapp.mybatis.interceptor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import org.apache.ibatis.executor.parameter.DefaultParameterHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xllapp.mybatis.Page;
import org.xllapp.mybatis.dialect.Dialect;
import org.xllapp.mybatis.dialect.MySql5Dialect;
import org.xllapp.mybatis.dialect.OracleDialect;

/**
 * 
 * @author dylan.chen Mar 9, 2013
 * 
 */
@Intercepts({ @Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class }) })
public class PaginationInterceptor implements Interceptor {

	private final static Logger logger = LoggerFactory.getLogger(PaginationInterceptor.class);

	@Override
	public Object intercept(Invocation invocation) throws Throwable {

		StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
		if (!(statementHandler instanceof RoutingStatementHandler)) {
			return invocation.proceed();
		}

		BoundSql boundSql = statementHandler.getBoundSql();
		Page<?> page = PageHolder.getPage();
		if (null == page) { // 判断当前查询是否需要分页
			return invocation.proceed();
		}

		MetaObject metaStatementHandler = MetaObject.forObject(statementHandler); // 便于使用Spring风格的bean的属性访问方式.
		Configuration configuration = (Configuration) metaStatementHandler.getValue("delegate.configuration"); // 通过反射方式获取statementHandler对象中属性delegate的属性configuration的值

		Dialect dialect = getDialect(configuration);

		String originalSql = (String) metaStatementHandler.getValue("delegate.boundSql.sql");

		page.setTotalCount(getTotalCount(dialect, invocation, configuration, metaStatementHandler, boundSql, originalSql));

		// 构建分页查询SQL
		metaStatementHandler.setValue("delegate.boundSql.sql", dialect.getLimitString(originalSql, (int) page.getFirst() - 1, page.getPageSize()));
		metaStatementHandler.setValue("delegate.rowBounds.offset", RowBounds.NO_ROW_OFFSET);
		metaStatementHandler.setValue("delegate.rowBounds.limit", RowBounds.NO_ROW_LIMIT);
		
		if (logger.isDebugEnabled()) {
			logger.debug("page sql: {}", boundSql.getSql());
		}
		
		return invocation.proceed();
	}

	private Dialect getDialect(Configuration configuration) {

		Dialect.Type databaseType = null;
		try {
			databaseType = Dialect.Type.valueOf(configuration.getVariables().getProperty("dialect").toUpperCase());
		} catch (Exception e) {
			throw new RuntimeException("invalid dialect[" + databaseType + "]");
		}

		Dialect dialect = null;
		switch (databaseType) {
		case MYSQL:
			dialect = new MySql5Dialect();
			break;
		case ORACLE:
			dialect = new OracleDialect();
			break;
		}
		
		return dialect;
	}

	private long getTotalCount(Dialect dialect, Invocation invocation, Configuration configuration, MetaObject metaStatementHandler, BoundSql boundSql, String originalSql) throws Exception {
		MappedStatement mappedStatement = (MappedStatement) metaStatementHandler.getValue("delegate.mappedStatement");
		Connection connection = (Connection) invocation.getArgs()[0];
		Object parameterObject = boundSql.getParameterObject();
		String countSql = dialect.getCountString(originalSql);
		logger.debug("count sql: {}", countSql);
		PreparedStatement countStmt = connection.prepareStatement(countSql);
		BoundSql countBS = new BoundSql(configuration, countSql, boundSql.getParameterMappings(), parameterObject);
		MetaObject.forObject(countBS).setValue("metaParameters", MetaObject.forObject(boundSql).getValue("metaParameters"));// 用于复制附加参数，主要处理如in等的参数
		new DefaultParameterHandler(mappedStatement, parameterObject, countBS).setParameters(countStmt);
		ResultSet rs = countStmt.executeQuery();
		int count = 0;
		if (rs.next()) {
			count = rs.getInt(1);
		}
		rs.close();
		countStmt.close();
		logger.debug("result count:{}", count);
		return count;
	}

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {
	}

}
