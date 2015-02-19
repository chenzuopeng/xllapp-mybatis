package org.xllapp.mybatis.support;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.apache.ibatis.binding.BindingException;
import org.apache.ibatis.session.SqlSession;

/**
 * 使用自定义的ICityMapperMethod类.基于Mybatis3.1.1版本的MapperProxy类进行修改.
 * 
 * @author dylan.chen Mar 23, 2014
 * 
 */
public class ICityMapperProxy implements InvocationHandler, Serializable {

	private static final long serialVersionUID = -625338673818157753L;

	private SqlSession sqlSession;

	private <T> ICityMapperProxy(SqlSession sqlSession) {
		this.sqlSession = sqlSession;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (method.getDeclaringClass() == Object.class) {
			return method.invoke(this, args);
		}
		final Class<?> declaringInterface = findDeclaringInterface(proxy, method);

		// final MapperMethod mapperMethod = new MapperMethod(declaringInterface, method, sqlSession);
        
		/*修改开始*/
		final ICityMapperMethod mapperMethod = new ICityMapperMethod(declaringInterface, method, this.sqlSession);  //使用自定义的MapperMethod类
		/*修改结束*/
		
		final Object result = mapperMethod.execute(args);
		if (result == null && method.getReturnType().isPrimitive() && !method.getReturnType().equals(Void.TYPE)) {
			throw new BindingException("Mapper method '" + method.getName() + "' (" + method.getDeclaringClass() + ") attempted to return null from a method with a primitive return type (" + method.getReturnType() + ").");
		}
		return result;
	}

	private Class<?> findDeclaringInterface(Object proxy, Method method) {
		Class<?> declaringInterface = null;
		for (Class<?> iface : proxy.getClass().getInterfaces()) {
			try {
				Method m = iface.getMethod(method.getName(), method.getParameterTypes());
				if (declaringInterface != null) {
					throw new BindingException("Ambiguous method mapping.  Two mapper interfaces contain the identical method signature for " + method);
				} else if (m != null) {
					declaringInterface = iface;
				}
			} catch (Exception e) {
				// Intentionally ignore.
				// This is using exceptions for flow control,
				// but it's definitely faster.
			}
		}
		if (declaringInterface == null) {
			throw new BindingException("Could not find interface with the given method " + method);
		}
		return declaringInterface;
	}

	@SuppressWarnings("unchecked")
	public static <T> T newMapperProxy(Class<T> mapperInterface, SqlSession sqlSession) {
		ClassLoader classLoader = mapperInterface.getClassLoader();
		Class<?>[] interfaces = new Class[] { mapperInterface };
		ICityMapperProxy proxy = new ICityMapperProxy(sqlSession);
		return (T) Proxy.newProxyInstance(classLoader, interfaces, proxy);
	}

	/*
	 * private MapperProxy internalMapperProxy;
	 * 
	 * public ICityMapperProxy(MapperProxy internalMapperProxy) {
	 * this.internalMapperProxy = internalMapperProxy; }
	 * 
	 * @Override public Object invoke(Object proxy, Method method, Object[]
	 * args) throws Throwable {
	 * 
	 * if (ArrayUtils.isEmpty(args)) { return
	 * this.internalMapperProxy.invoke(proxy, method, args); }
	 * 
	 * List<Object> list = new ArrayList<Object>(); for (Object arg : args) { if
	 * (!(arg instanceof Page)) { list.add(arg); } } Object[] newArgs =
	 * list.toArray(new Object[0]);
	 * 
	 * if (logger.isDebugEnabled()) { logger.debug("origArgs:{},newArgs:{}",
	 * Arrays.toString(args), Arrays.toString(newArgs)); }
	 * 
	 * return this.internalMapperProxy.invoke(proxy, method, newArgs); }
	 * 
	 * @SuppressWarnings("unchecked") public static <T> T
	 * newMapperProxy(Class<T> mapperInterface, SqlSession sqlSession) {
	 * ClassLoader classLoader = mapperInterface.getClassLoader(); Class<?>[]
	 * interfaces = new Class[] { mapperInterface }; InvocationHandler proxy =
	 * new ICityMapperProxy(newInternalMapperProxy(sqlSession)); return (T)
	 * Proxy.newProxyInstance(classLoader, interfaces, proxy); }
	 * 
	 * private static MapperProxy newInternalMapperProxy(SqlSession sqlSession)
	 * { try { Constructor<MapperProxy> constructor =
	 * MapperProxy.class.getDeclaredConstructor(SqlSession.class);
	 * constructor.setAccessible(true); return
	 * constructor.newInstance(sqlSession); } catch (Exception e) { throw new
	 * RuntimeException(e.getLocalizedMessage(), e); } }
	 */
}
