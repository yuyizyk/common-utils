package cn.yuyizyk.tools.clz.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import cn.yuyizyk.tools.clz.proxy.ProxyClzUtils.Invoke;

/**
 * 通过jdk动态代理
 * 
 * @author yuyi
 *
 */
public class JdkProxyUtils implements InvocationHandler {
	//private final static Logger log = LoggerFactory.getLogger(JdkProxyUtils.class);
	private Object targetObject;// 需要代理的目标对象

	private Invoke proxInvoke;

	/**
	 * 
	 * @param targetObject
	 * @param invocationHandler
	 * @return
	 */
	public static final Object createProxy(Object targetObject, Invoke proxInvoke) {// 将目标对象传入进行代理
		JdkProxyUtils proxy = new JdkProxyUtils();
		proxy.targetObject = targetObject;
		proxy.proxInvoke = proxInvoke;
		return Proxy.newProxyInstance(targetObject.getClass().getClassLoader(), targetObject.getClass().getInterfaces(),
				proxy);// 返回代理对象
	}

	@Override
	public Object invoke(Object proxyObject, Method method, Object[] args) throws Throwable {
		return proxInvoke.apply(proxyObject, targetObject, method, args);
	}

	private JdkProxyUtils() {
	}
}
