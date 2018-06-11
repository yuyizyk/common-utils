package cn.yuyizyk.tools.lang;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.util.ClassUtil;

import cn.yuyizyk.tools.clz.Clzs;
import cn.yuyizyk.tools.clz.ReflectUtils;
import cn.yuyizyk.tools.exception.Exceptions;
import cn.yuyizyk.tools.io.FastByteArrayOutputStream;

/**
 * Object Utils
 * 
 * <p>
 * 参考：
 * <ul>
 * <li>https://github.com/looly/hutool/tree/v4-master/hutool-core/src/main/java/cn/hutool/core/util</li>
 * </ul>
 * </p>
 *
 * @author yuyi
 */
public class Objs {
	private final static Logger log = LoggerFactory.getLogger(Objs.class);

	/**
	 * 克隆对象<br>
	 * 如果对象实现Cloneable接口，调用其clone方法<br>
	 * 如果实现Serializable接口，执行深度克隆<br>
	 * 否则返回<code>null</code>
	 * 
	 * @param <T>
	 *            对象类型
	 * @param obj
	 *            被克隆对象
	 * @return 克隆后的对象
	 */
	public static <T> T clone(T obj) {
		T result = null;
		if (obj instanceof Cloneable) {
			result = ReflectUtils.invoke(obj, "clone", new Object[] {});
		} else {
			result = cloneByStream(obj);
		}
		return result;
	}

	/**
	 * 克隆对象<br>
	 * 如果对象实现Cloneable接口，调用其clone方法<br>
	 * 如果实现Serializable接口，执行深度克隆<br>
	 * 否则返回<code>null</code>
	 * 
	 * @param <T>
	 *            对象类型
	 * @param obj
	 *            被克隆对象
	 * @return 克隆后的对象
	 */
	@SuppressWarnings("unchecked")
	public static <T> T clone(T[] obj) {
		final Object result;
		final Class<?> componentType = obj.getClass().getComponentType();
		if (componentType.isPrimitive()) {// 原始类型
			int length = Array.getLength(obj);
			result = Array.newInstance(componentType, length);
			while (length-- > 0) {
				Array.set(result, length, Array.get(obj, length));
			}
		} else {
			result = ((Object[]) obj).clone();
		}
		return (T) result;
	}

	/**
	 * 深拷贝
	 * 
	 * @param src
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static <T> T cloneByStream(T src) {
		if (Objects.isNull(src) || !(src instanceof Serializable)) {
			return null;
		}
		final ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		try (ObjectOutputStream out = new ObjectOutputStream(byteOut)) {
			out.writeObject(src);

			ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
			ObjectInputStream in = new ObjectInputStream(byteIn);
			@SuppressWarnings("unchecked")
			T dest = (T) in.readObject();
			return dest;
		} catch (IOException | ClassNotFoundException e) {
			log.error(" cloneByStream error ", e);
			throw Exceptions.box(e);
		}
	}

	/**
	 * <pre>
	 * isEmpty(null)      = true
	 * isEmpty([])        = true
	 * isEmpty([null])       = false
	 * </pre>
	 * 
	 * @param args
	 * @return
	 */
	public static final boolean isEmpty(final Object[] args) {
		return args == null || args.length == 0;
	}

	/**
	 * <pre>
	 * isEmpty(null)      = true
	 * isEmpty([])        = true
	 * isEmpty([null])       = false
	 * </pre>
	 * 
	 * @param args
	 * @return
	 */
	public static final boolean isEmpty(final Collection<?> args) {
		return args == null || args.isEmpty();
	}

	/**
	 * <pre>
	 * isEmpty(null)      = true
	 * isEmpty({})        = true
	 * isEmpty({null=null})       = false
	 * </pre>
	 * 
	 * @param args
	 * @return
	 */
	public static final boolean isEmpty(final Map<?, ?> args) {
		return args == null || args.isEmpty();
	}

	/**
	 * 
	 * @param args
	 * @return args == null
	 */
	public static final boolean isEmpty(final Object args) {
		return Objects.isNull(args);
	}

	/**
	 * <pre>
	 * isEmpty(null)      = true
	 * isEmpty("")        = true
	 * isEmpty(" ")       = false
	 * isEmpty("bob")     = false
	 * isEmpty("  bob  ") = false
	 * </pre>
	 * 
	 * @param args
	 * @return
	 */
	public static final boolean isEmpty(final CharSequence cs) {
		return Strs.isEmpty(cs);
	}

	/**
	 * 数组是否为空
	 * 
	 * @param array
	 *            数组
	 * @return 是否为空
	 */
	public static boolean isEmpty(final long... array) {
		return array == null || array.length == 0;
	}

	/**
	 * 数组是否为空
	 * 
	 * @param array
	 *            数组
	 * @return 是否为空
	 */
	public static boolean isEmpty(final int... array) {
		return array == null || array.length == 0;
	}

	/**
	 * 数组是否为空
	 * 
	 * @param array
	 *            数组
	 * @return 是否为空
	 */
	public static boolean isEmpty(final short... array) {
		return array == null || array.length == 0;
	}

	/**
	 * 数组是否为空
	 * 
	 * @param array
	 *            数组
	 * @return 是否为空
	 */
	public static boolean isEmpty(final char... array) {
		return array == null || array.length == 0;
	}

	/**
	 * 数组是否为空
	 * 
	 * @param array
	 *            数组
	 * @return 是否为空
	 */
	public static boolean isEmpty(final byte... array) {
		return array == null || array.length == 0;
	}

	/**
	 * 数组是否为空
	 * 
	 * @param array
	 *            数组
	 * @return 是否为空
	 */
	public static boolean isEmpty(final double... array) {
		return array == null || array.length == 0;
	}

	/**
	 * 数组是否为空
	 * 
	 * @param array
	 *            数组
	 * @return 是否为空
	 */
	public static boolean isEmpty(final float... array) {
		return array == null || array.length == 0;
	}

	/**
	 * 数组是否为空
	 * 
	 * @param array
	 *            数组
	 * @return 是否为空
	 */
	public static boolean isEmpty(final boolean... array) {
		return array == null || array.length == 0;
	}

	/**
	 * 包含{@code null}元素，或 数组为空{@code null}
	 * 
	 * @param array
	 * @return
	 */
	public static boolean anyNull(Object... array) {
		return isEmpty(array) || Stream.of(array).anyMatch(Objs::isEmpty);
	}

	/**
	 * 序列化<br>
	 * 对象必须实现Serializable接口
	 * 
	 * @param <T>
	 *            对象类型
	 * @param obj
	 *            要被序列化的对象
	 * @return 序列化后的字节码
	 */
	public static <T> byte[] serialize(T obj) {
		if (null == obj || false == (obj instanceof Serializable)) {
			return null;
		}
		FastByteArrayOutputStream byteOut = new FastByteArrayOutputStream();
		try (ObjectOutputStream oos = new ObjectOutputStream(byteOut)) {
			oos.writeObject(obj);
			oos.flush();
		} catch (Exception e) {
			throw Exceptions.box(e, true);
		}
		return byteOut.toByteArray();
	}

	/**
	 * 反序列化<br>
	 * 对象必须实现Serializable接口
	 * 
	 * @param <T>
	 *            对象类型
	 * @param bytes
	 *            反序列化的字节码
	 * @return 反序列化后的对象
	 */
	@SuppressWarnings("unchecked")
	public static <T> T unserialize(byte[] bytes) {
		ObjectInputStream ois = null;
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
			ois = new ObjectInputStream(bais);
			return (T) ois.readObject();
		} catch (Exception e) {
			throw Exceptions.box(e, true);
		}
	}

	/**
	 * 是否为基本类型，包括包装类型和非包装类型
	 * 
	 * @see ClassUtil#isBasicType(Class)
	 * @param object
	 *            被检查对象
	 * @return 是否为基本类型
	 */
	public static boolean isBasicType(Object object) {
		return Clzs.isBasicType(object.getClass());
	}

	/**
	 * 检查是否为有效的数字<br>
	 * 检查Double和Float是否为无限大，或者Not a Number<br>
	 * 非数字类型和Null将返回true
	 * 
	 * @param obj
	 *            被检查类型
	 * @return 检查结果，非数字类型和Null将返回true
	 */
	public static boolean isValidIfNumber(Object obj) {
		if (obj != null && obj instanceof Number) {
			if (((Double) obj).isInfinite() || ((Double) obj).isNaN()) {
				if (obj instanceof Double) {
					return false;
				}
			} else if (obj instanceof Float) {
				if (((Float) obj).isInfinite() || ((Float) obj).isNaN()) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 获得给定类的第一个泛型参数
	 * 
	 * @param obj
	 *            被检查的对象
	 * @return {@link Class}
	 */
	public static Class<?> getTypeArgument(Object obj) {
		return getTypeArgument(obj, 0);
	}

	/**
	 * 获得给定类的泛型参数
	 * 
	 * @param obj
	 *            被检查的对象
	 * @param index
	 *            泛型类型的索引号，既第几个泛型类型
	 * @return {@link Class}
	 */
	public static Class<?> getTypeArgument(Object obj, int index) {
		return Clzs.getTypeArgument(obj.getClass(), index);
	}

}
