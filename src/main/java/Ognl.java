import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

import org.xllapp.mybatis.MybatisContext;

/**
 * Ognl工具类，主要是为了在ognl表达式访问静态方法时可以减少长长的类名称编写 Ognl访问静态方法的表达式为: @class@method(args)
 * 
 * 示例使用:
 * 
 * <pre>
 * 	&lt;if test="@Ognl@isNotEmpty(userId)">
 * 	and user_id = #{userId}
 * &lt;/if>
 * </pre>
 * 
 * @author badqiu
 * 
 */
public abstract class Ognl {

	/**
	 * 判断String,Map,Collection,Array对象是否为空.
	 */
	public static boolean isEmpty(Object o) throws IllegalArgumentException {
		if (o == null) {
			return true;
		}

		if (o instanceof String) {
			if (((String) o).length() == 0) {
				return true;
			}
		} else if (o instanceof Collection) {
			if (((Collection<?>) o).isEmpty()) {
				return true;
			}
		} else if (o.getClass().isArray()) {
			if (Array.getLength(o) == 0) {
				return true;
			}
		} else if (o instanceof Map) {
			if (((Map<?,?>) o).isEmpty()) {
				return true;
			}
		} else {
			return false;
			// throw new
			// IllegalArgumentException("Illegal argument type,must be : Map,Collection,Array,String. but was:"+o.getClass());
		}

		return false;
	}

	/**
	 * 可以用于判断 Map,Collection,String,Array是否不为空.
	 */
	public static boolean isNotEmpty(Object o) {
		return !isEmpty(o);
	}

	/**
	 * 判断是否为数字.
	 */
	public static boolean isNumber(Object o) {
		if (o == null) {
			return false;
		}
		if (o instanceof Number) {
			return true;
		}
		if (o instanceof String) {
			String str = (String) o;
			if (str.length() == 0) {
				return false;
			}
			if (str.trim().length() == 0) {
				return false;
			}

			try {
				Double.parseDouble(str);
				return true;
			} catch (NumberFormatException e) {
				return false;
			}
		}
		return false;
	}

	/**
	 * 判断String对象是否为空,非String对象返回false.
	 */
	public static boolean isBlank(Object o) {
		if (o == null) {
			return true;
		}
		if (o instanceof String) {
			String str = (String) o;
			return isBlank(str);
		}
		return false;
	}
	
	/**
	 * 判断String对象是否不为空,非String对象返回true.
	 */
	public static boolean isNotBlank(Object o) {
		return !isBlank(o);
	}

	/**
	 * 判断String对象是否为空.
	 */
	public static boolean isBlank(String str) {
		if (str == null || str.length() == 0) {
			return true;
		}

		for (int i = 0; i < str.length(); i++) {
			if (!Character.isWhitespace(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 验证当前是否激活给定的环境配置.
	 */
	public static boolean isActivatedEnvironment(String input){
		return MybatisContext.isActivatedEnvironment(input);
	}

}
