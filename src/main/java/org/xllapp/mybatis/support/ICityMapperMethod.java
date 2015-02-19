package org.xllapp.mybatis.support;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.binding.BindingException;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.xllapp.mybatis.Page;
import org.xllapp.mybatis.interceptor.PageHolder;

/**
 * 过滤方法参数中的Page对象. 基于Mybatis3.1.1版本的MapperMethod类进行修改.
 * 
 * 当映射的方法有多个参数时,在mapper文件使用#{paramN}方式引用参数时,需要忽略掉Page类型参数.
 * 
 * 如方法query(Object object1,Page<?> page,Object
 * object2),在mapper文件中使用#{param2}引用object2,而不是#{param3}.
 * 
 * @author dylan.chen Mar 23, 2014
 * 
 */
public class ICityMapperMethod {

	private final SqlSession sqlSession;
	private final Configuration config;
	private final ObjectFactory objectFactory;

	private SqlCommandType type;
	private String commandName;

	private Class<?> declaringInterface;
	private Method method;

	private boolean returnsMany;
	private boolean returnsMap;
	private boolean returnsVoid;
	private String mapKey;

	private Integer resultHandlerIndex;
	private Integer rowBoundsIndex;
	private List<String> paramNames;
	private List<Integer> paramPositions;

	private boolean hasNamedParameters;

	/* 修改开始 */
	private Integer pageIndex = -1; // 第一个Page类型参数的位置
	/* 修改结束 */

	public ICityMapperMethod(Class<?> declaringInterface, Method method, SqlSession sqlSession) {
		paramNames = new ArrayList<String>();
		paramPositions = new ArrayList<Integer>();
		this.sqlSession = sqlSession;
		this.method = method;
		this.config = sqlSession.getConfiguration();
		this.hasNamedParameters = false;
		this.declaringInterface = declaringInterface;
		this.objectFactory = config.getObjectFactory();
		setupFields();
		setupMethodSignature();
		setupCommandType();
		validateStatement();
	}

	public Object execute(Object[] args) {
		
		Object result = null;
		if (SqlCommandType.INSERT == type) {
			Object param = getParam(args);
			result = sqlSession.insert(commandName, param);
		} else if (SqlCommandType.UPDATE == type) {
			Object param = getParam(args);
			result = sqlSession.update(commandName, param);
		} else if (SqlCommandType.DELETE == type) {
			Object param = getParam(args);
			result = sqlSession.delete(commandName, param);
		} else if (SqlCommandType.SELECT == type) {
			
			/* 修改开始 */
			if (pageIndex > -1) {
				PageHolder.setPage((Page<?>) args[pageIndex]);
			}
			/* 修改结束 */
			
			if (returnsVoid && resultHandlerIndex != null) {
				executeWithResultHandler(args);
			} else if (returnsMany) {
				result = executeForMany(args);
			} else if (returnsMap) {
				result = executeForMap(args);
			} else {
				Object param = getParam(args);
				result = sqlSession.selectOne(commandName, param);
			}
		} else {
			throw new BindingException("Unknown execution method for: " + commandName);
		}

		return result;
	}

	private void executeWithResultHandler(Object[] args) {
		MappedStatement ms = config.getMappedStatement(commandName);
		if (Void.TYPE.equals(ms.getResultMaps().get(0).getType())) {
			throw new BindingException("method " + method.getName() + " needs either a @ResultMap annotation or a resultType attribute in XML so a ResultHandler can be used as a parameter.");
		}
		Object param = getParam(args);
		if (rowBoundsIndex != null) {
			RowBounds rowBounds = (RowBounds) args[rowBoundsIndex];
			sqlSession.select(commandName, param, rowBounds, (ResultHandler) args[resultHandlerIndex]);
		} else {
			sqlSession.select(commandName, param, (ResultHandler) args[resultHandlerIndex]);
		}
	}

	private <E> Object executeForMany(Object[] args) {
		List<E> result;
		Object param = getParam(args);
		if (rowBoundsIndex != null) {
			RowBounds rowBounds = (RowBounds) args[rowBoundsIndex];
			result = sqlSession.<E> selectList(commandName, param, rowBounds);
		} else {
			result = sqlSession.<E> selectList(commandName, param);
		}
		// issue #510 Collections & arrays support
		if (!method.getReturnType().isAssignableFrom(result.getClass())) {
			if (method.getReturnType().isArray()) {
				return convertToArray(result);
			} else {
				return convertToDeclaredCollection(result);
			}
		}
		return result;
	}

	private <E> Object convertToDeclaredCollection(List<E> list) {
		Object collection = objectFactory.create(method.getReturnType());
		MetaObject metaObject = config.newMetaObject(collection);
		metaObject.addAll(list);
		return collection;
	}

	@SuppressWarnings("unchecked")
	private <E> E[] convertToArray(List<E> list) {
		E[] array = (E[]) Array.newInstance(method.getReturnType().getComponentType(), list.size());
		array = list.toArray(array);
		return array;
	}

	private <K, V> Map<K, V> executeForMap(Object[] args) {
		Map<K, V> result;
		Object param = getParam(args);
		if (rowBoundsIndex != null) {
			RowBounds rowBounds = (RowBounds) args[rowBoundsIndex];
			result = sqlSession.<K, V> selectMap(commandName, param, mapKey, rowBounds);
		} else {
			result = sqlSession.<K, V> selectMap(commandName, param, mapKey);
		}
		return result;
	}

	private Object getParam(Object[] args) {
		final int paramCount = paramPositions.size();
		if (args == null || paramCount == 0) {
			return null;
		} else if (!hasNamedParameters && paramCount == 1) {
			return args[paramPositions.get(0)];
		} else {
			Map<String, Object> param = new MapperMethod.MapperParamMap<Object>();
			for (int i = 0; i < paramCount; i++) {
				param.put(paramNames.get(i), args[paramPositions.get(i)]);
			}
			// issue #71, add param names as param1, param2...but ensure
			// backward compatibility
			for (int i = 0; i < paramCount; i++) {
				String genericParamName = "param" + String.valueOf(i + 1);
				if (!param.containsKey(genericParamName)) {
					param.put(genericParamName, args[paramPositions.get(i)]);
				}
			}
			return param;
		}
	}

	// Setup //

	private void setupFields() {
		this.commandName = declaringInterface.getName() + "." + method.getName();
	}

	private void setupMethodSignature() {
		if (method.getReturnType().equals(Void.TYPE)) {
			returnsVoid = true;
		}
		if (objectFactory.isCollection(method.getReturnType()) || method.getReturnType().isArray()) {
			returnsMany = true;
		}
		if (Map.class.isAssignableFrom(method.getReturnType())) {
			final MapKey mapKeyAnnotation = method.getAnnotation(MapKey.class);
			if (mapKeyAnnotation != null) {
				mapKey = mapKeyAnnotation.value();
				returnsMap = true;
			}
		}
		final Class<?>[] argTypes = method.getParameterTypes();
		for (int i = 0; i < argTypes.length; i++) {

			/* 修改开始 */
			if (Page.class.isAssignableFrom(argTypes[i])) { // 过滤方法参数中的Page对象
				if (pageIndex == -1) { // 只有第一个Page类型参数生效
					pageIndex = i;
				}
				continue;
			}
			/* 修改结束 */

			if (RowBounds.class.isAssignableFrom(argTypes[i])) {
				if (rowBoundsIndex == null) {
					rowBoundsIndex = i;
				} else {
					throw new BindingException(method.getName() + " cannot have multiple RowBounds parameters");
				}
			} else if (ResultHandler.class.isAssignableFrom(argTypes[i])) {
				if (resultHandlerIndex == null) {
					resultHandlerIndex = i;
				} else {
					throw new BindingException(method.getName() + " cannot have multiple ResultHandler parameters");
				}
			} else {
				String paramName = String.valueOf(paramPositions.size());
				paramName = getParamNameFromAnnotation(i, paramName);
				paramNames.add(paramName);
				paramPositions.add(i);
			}
		}
	}

	private String getParamNameFromAnnotation(int i, String paramName) {
		Object[] paramAnnos = method.getParameterAnnotations()[i];
		for (int j = 0; j < paramAnnos.length; j++) {
			if (paramAnnos[j] instanceof Param) {
				hasNamedParameters = true;
				paramName = ((Param) paramAnnos[j]).value();
			}
		}
		return paramName;
	}

	private void setupCommandType() {
		MappedStatement ms = config.getMappedStatement(commandName);
		type = ms.getSqlCommandType();
		if (type == SqlCommandType.UNKNOWN) {
			throw new BindingException("Unknown execution method for: " + commandName);
		}
	}

	private void validateStatement() {
		try {
			config.getMappedStatement(commandName);
		} catch (Exception e) {
			throw new BindingException("Invalid bound statement (not found): " + commandName, e);
		}
	}

}
