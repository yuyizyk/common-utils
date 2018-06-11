package cn.yuyizyk.tools.clz;

import java.net.URL;
import java.net.URLDecoder;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 
 * 
 *
 * @author yuyi
 */
public class ClassLoaderUtils {
	private static final Logger logger = LoggerFactory.getLogger(ClassLoaderUtils.class);

	private static String classPath = "";

	static {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		if (loader == null) {
			logger.warn("Using system class loader");
			loader = ClassLoader.getSystemClassLoader();
		}

		try {
			URL url = loader.getResource("");
			// get class path
			if (url != null) {
				classPath = url.getPath();
				classPath = URLDecoder.decode(classPath, "utf-8");
			}

			// 如果是jar包内的，则返回当前路径
			if (StringUtils.isBlank(classPath) || classPath.contains(".jar!")) {
				classPath = System.getProperty("user.dir");
			}
		} catch (Throwable ex) {
			classPath = System.getProperty("user.dir");
			logger.warn("Failed to locate class path, fallback to user.dir: {}", classPath, ex);
		}
	}

	public static String getClassPath() {
		return classPath;
	}

	/**
	 * 指定类是否被提供<br>
	 * 通过调用{@link Class.forName(String)}方法尝试加载指定类名的类，如果加载失败返回false<br>
	 * 加载失败的原因可能是此类不存在或其关联引用类不存在
	 * 
	 * @param className
	 *            类名
	 * @param classLoader
	 *            {@link ClassLoader}
	 * @return 是否被提供
	 */
	public static boolean isClassPresent(String className) {
		try {
			Class.forName(className);
			return true;
		} catch (ClassNotFoundException ex) {
			return false;
		}
	}

	/**
	 * 获取当前线程的{@link ClassLoader}
	 * 
	 * @return 当前线程的class loader
	 * @see Thread#getContextClassLoader()
	 */
	public static ClassLoader getContextClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}

	/**
	 * 获取{@link ClassLoader}<br>
	 * 获取顺序如下：<br>
	 * 
	 * <pre>
	 * 1、获取当前线程的ContextClassLoader
	 * 2、获取{@link ClassLoaderUtils}类对应的ClassLoader
	 * 3、获取系统ClassLoader（{@link ClassLoader#getSystemClassLoader()}）
	 * </pre>
	 * 
	 * @return 类加载器
	 */
	public static ClassLoader getClassLoader() {
		ClassLoader classLoader = getContextClassLoader();
		if (classLoader == null) {
			classLoader = ClassLoaderUtils.class.getClassLoader();
			if (null == classLoader) {
				classLoader = ClassLoader.getSystemClassLoader();
			}
		}
		return classLoader;
	}

	public static Class<?> loadClass(String clzname) {
		try {
			return getClassLoader().loadClass(clzname);
		} catch (ClassNotFoundException e) {
		}
		return null;
	}

}
