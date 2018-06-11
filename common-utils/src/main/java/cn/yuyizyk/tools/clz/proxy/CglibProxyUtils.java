package cn.yuyizyk.tools.clz.proxy;

import java.lang.reflect.Method;

import org.junit.Test;

import cn.yuyizyk.tools.clz.proxy.ProxyClzUtils.Invoke;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * 通过cglib动态代理
 * 
 * @author yuyi
 *
 */
public class CglibProxyUtils implements MethodInterceptor {
	// private final static Logger log =
	// LoggerFactory.getLogger(CglibProxyUtils.class);

	public static interface Intercept {
		public Object apply(Object proxy, Object src, Method method, Object[] args, MethodProxy methodProxy);
	}

	static interface UserManager {
		void addUser(String k, String v);
	}

	static class UserManagerImpl implements UserManager {
		@Override
		public void addUser(String k, String v) {
			System.out.println(k + "   " + v);
		}

	}

	@Test
	public void test() {
		UserManager userManager = ProxyClzUtils.createProxy(UserManager.class, new UserManagerImpl(), Invoke.defualt);
		userManager.addUser("tom", "root1");
		UserManagerImpl userManagerJDK = ProxyClzUtils.createProxy(UserManagerImpl.class, new UserManagerImpl(),
				Invoke.defualt);
		userManagerJDK.addUser("tom", "root2");
	}

	private Object targetObject;// CGLib需要代理的目标对象
	private Invoke proxInvoke;
	private Intercept intercept;

	@Override
	public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
		if (intercept != null)
			return intercept.apply(proxy, targetObject, method, args, methodProxy);
		return proxInvoke.apply(proxy, targetObject, method, args);
	}

	/**
	 * 
	 * @param targetObject
	 * @param proxInvoke
	 * @return
	 */
	public static final Object createProxy(Object implObj, Intercept intercept) {
		CglibProxyUtils proxy = new CglibProxyUtils();
		proxy.targetObject = implObj;
		proxy.intercept = intercept;
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(implObj.getClass());
		enhancer.setCallback(proxy);
		return enhancer.create();// 返回代理对象
	}

	/**
	 * 
	 * @param targetObject
	 * @param proxInvoke
	 * @return
	 */
	public static final Object createProxy(Object implObj, Invoke proxyInvoke) {
		CglibProxyUtils proxy = new CglibProxyUtils();
		proxy.targetObject = implObj;
		proxy.proxInvoke = proxyInvoke;
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(implObj.getClass());
		enhancer.setCallback(proxy);
		return enhancer.create();// 返回代理对象
	}
}
