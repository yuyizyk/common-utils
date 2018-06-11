package cn.yuyizyk.tools.clz.proxy;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 通过net.sf.cglib.proxy.Enhancer 动态创建子类 方式对原class 进行代理
 * 
 * @author yuyi
 *
 */

public final class ProxyClzUtils {
	final static Logger log = LoggerFactory.getLogger(ProxyClzUtils.class);

	public static interface Invoke {
		public Object apply(Object proxyObject, Object targetObject, Method method, Object[] args) throws Throwable;

		public static final Invoke defualt = (proxy, target, method, args) -> method.invoke(target, args);
	}

	/**
	 * 
	 * @param returnClz
	 * @param implObj
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T createProxy(Class<T> returnClz, T implObj, Invoke proxyInvoke) {
		if (returnClz.isInterface()) {
			return (T) JdkProxyUtils.createProxy(implObj, proxyInvoke);
		}
		assert (returnClz.getModifiers() & Modifier.FINAL) != 1;
		return (T) CglibProxyUtils.createProxy(implObj, proxyInvoke);
	}

}
