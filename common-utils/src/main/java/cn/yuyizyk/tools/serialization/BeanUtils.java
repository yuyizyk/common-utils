package cn.yuyizyk.tools.serialization;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.yuyizyk.tools.clz.Clzs;
import cn.yuyizyk.tools.lang.Dates;

/**
 * 
 * @author yuyi
 *
 */
public class BeanUtils {
	private final static Logger log = LoggerFactory.getLogger(BeanUtils.class);

	/**
	 * 判断是否为Bean对象<br>
	 * 判定方法是是否存在只有一个参数的setXXX方法
	 * 
	 * @param clazz
	 *            待测试类
	 * @return 是否为Bean对象
	 */
	public static boolean isBean(Class<?> clazz) {
		if (Clzs.isNormalClass(clazz)) {
			final Method[] methods = clazz.getMethods();
			for (Method method : methods) {
				if (method.getParameterTypes().length == 1 && method.getName().startsWith("set")) {
					// 检测包含标准的setXXX方法即视为标准的JavaBean
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 查询对应field
	 * 
	 * @param clazz
	 * @param key
	 * @return
	 */
	public static Field deepFindField(Class<? extends Object> clazz, String key) {
		Field field = null;
		while (!clazz.getName().equals(Object.class.getName())) {
			try {
				field = clazz.getDeclaredField(key);
				if (field != null) {
					break;
				}
			} catch (Exception e) {
				clazz = clazz.getSuperclass();
			}
		}
		return field;
	}

	/**
	 * 根据列表里面的属性聚合
	 *
	 * <pre>
	 * List<ShopDTO> shopList = shopService.queryShops();
	 * Map<Integer, List<ShopDTO>> city2Shops = BeanUtil.aggByKeyToList("cityId", shopList);
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	public static <K, V> Map<K, List<V>> aggByKeyToList(String key, List<? extends Object> list) {
		Map<K, List<V>> map = new HashMap<K, List<V>>();
		if (list == null || list.isEmpty()) {// 防止外面传入空list
			return map;
		}
		try {
			Class<? extends Object> clazz = list.get(0).getClass();
			Field field = deepFindField(clazz, key);
			if (field == null)
				throw new IllegalArgumentException("Could not find the key");
			field.setAccessible(true);
			for (Object o : list) {
				K k = (K) field.get(o);
				if (map.get(k) == null) {
					map.put(k, new ArrayList<V>());
				}
				map.get(k).add((V) o);
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			log.error("", e);
		}
		return map;
	}

	/**
	 * 用于将一个对象的列表转换为列表中对象的属性集合
	 *
	 * <pre>
	 * List<UserDTO> userList = userService.queryUsers();
	 * Set<Integer> userIds = BeanUtil.toPropertySet("userId", userList);
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	public static <K> Set<K> toPropertySet(String key, List<? extends Object> list) {
		Set<K> set = new HashSet<K>();
		if (list == null || list.isEmpty()) {// 防止外面传入空list
			return set;
		}
		try {
			Class<? extends Object> clazz = list.get(0).getClass();
			Field field = deepFindField(clazz, key);
			if (field == null)
				throw new IllegalArgumentException("Could not find the key");
			field.setAccessible(true);
			for (Object o : list) {
				set.add((K) field.get(o));
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			log.error("", e);
		}
		return set;
	}

	/**
	 * 获取某个对象的某个属性
	 */
	public static Object getProperty(Object obj, String fieldName) {
		try {
			Field field = deepFindField(obj.getClass(), fieldName);
			if (field != null) {
				field.setAccessible(true);
				return field.get(obj);
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			log.error("", e);
		}
		return null;
	}

	/**
	 * 用于将一个列表转换为列表中的对象的某个属性映射到列表中的对象
	 *
	 * <pre>
	 * List<T> userList = userService.queryUsers();
	 * Map<Integer, T> userIdToUser = BeanUtil.mapByKey("userId", T);
	 * </pre>
	 *
	 * @param key
	 *            属性名
	 */
	@SuppressWarnings("unchecked")
	public static <K, V> Map<K, V> mapByKey(String key, List<? extends Object> list) {
		Map<K, V> map = new HashMap<K, V>();
		if (list == null || list.isEmpty()) {
			return map;
		}
		try {
			Class<? extends Object> clazz = list.get(0).getClass();
			Field field = deepFindField(clazz, key);
			if (field == null)
				throw new IllegalArgumentException("Could not find the key");
			field.setAccessible(true);
			for (Object o : list) {
				map.put((K) field.get(o), (V) o);
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			log.error("", e);
		}
		return map;
	}

	/**
	 * toMapByGetter <br/>
	 * 通过对象的getter 获得
	 * 
	 * @param obj
	 * @return
	 */
	public static final HashMap<String, Object> toMap(Object obj) {
		return toMap(obj, a -> {
			return true;
		}, a -> {
			return true;
		});
	}

	// @Test
	// public void test() throws IllegalArgumentException, IllegalAccessException {
	// System.out.println(BeanUtils.toMapByField(new BeanUtils()));
	// }

	/**
	 * 获得bean的属性值
	 * 
	 * @param a
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static final HashMap<String, Object> toMapByField(Object a)
			throws IllegalArgumentException, IllegalAccessException {
		HashMap<String, Object> map = new HashMap<>();
		Field[] field = a.getClass().getDeclaredFields();
		for (int i = 0; i < field.length; i++) {
			// 设置是否允许访问，不是修改原来的访问权限修饰词。
			field[i].setAccessible(true);
			// 返回输出指定对象a上此 Field表示的字段名和字段值
			map.put(field[i].getName(), field[i].get(a));
		}
		return map;
	}

	/**
	 * toMapByGetter <br/>
	 * 通过对象的getter 获得
	 * 
	 * @param obj
	 * @return
	 * @throws IntrospectionException
	 *             如果分析类属性失败
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 *             如果实例化 JavaBean 失败
	 * @throws InvocationTargetException
	 *             如果调用属性的 setter 方法失败
	 */
	public static final HashMap<String, Object> toMap(Object obj,
			final Function<PropertyDescriptor, Boolean> checkPropertyDescriptor,
			final Function<Object, Boolean> checkValue) {
		HashMap<String, Object> params = new HashMap<String, Object>(0);
		try {
			PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();
			PropertyDescriptor[] descriptors = propertyUtilsBean.getPropertyDescriptors(obj);
			String name;
			Object val;
			for (int i = 0; i < descriptors.length; i++) {
				name = descriptors[i].getName();
				if (!"class".equals(name) && checkPropertyDescriptor.apply(descriptors[i])) {
					val = propertyUtilsBean.getNestedProperty(obj, name);
					if (checkValue.apply(val))
						params.put(name, val);
				}
			}
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			log.error("toMap异常:obj:[{}]", obj, e);
		}
		return params;
	}

	/**
	 * 
	 * @param bean
	 * @param properties
	 * @throws IllegalAccessException
	 *             实例化 JavaBean 失败
	 * @throws InvocationTargetException
	 *             调用属性的 setter 方法失败
	 */
	public static void convert(Object bean, Map<String, ? extends Object> properties)
			throws IllegalAccessException, InvocationTargetException {
		BeanUtilsBean.getInstance().populate(bean, properties);
	}

	/**
	 * 通过 org.apache.commons.beanutils.Converter 对部分 进行自定义转化
	 * 
	 * @param bean
	 * @param properties
	 * @throws InstantiationException
	 *             如果分析类属性失败
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static <T> T convertMap(Class<T> clz, Map<String, ? extends Object> properties)
			throws InstantiationException, IllegalAccessException, InvocationTargetException {
		ConvertUtils.register(new MyTimestamp(), Timestamp.class); // 时间处理
		ConvertUtils.register(new MyDate(), Date.class);
		T bean = clz.newInstance();
		convert(bean, properties);
		return bean;
	}

	/**
	 * 根据同名setter 和getter进行设置
	 * 
	 * @param clz
	 * @param obj
	 * @return
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public static <T> T byBean(Class<T> type, Object src) {
		return byMap(type, toMap(src));
	}

	/**
	 * 将一个 Map 对象转化为一个 JavaBean <br/>
	 * java.beans.BeanInfo <br/>
	 * 适用于一般的bean转化 <br/>
	 * 通过setter
	 * 
	 * @param type
	 *            要转化的类型
	 * @param map
	 *            包含属性值的 map
	 * @return 转化出来的 JavaBean 对象
	 * @throws IllegalArgumentException
	 * @throws IntrospectionException
	 *             如果分析类属性失败
	 * @throws IllegalAccessException
	 *             如果实例化 JavaBean 失败
	 * @throws InstantiationException
	 *             如果实例化 JavaBean 失败
	 * @throws InvocationTargetException
	 *             如果调用属性的 setter 方法失败
	 */
	@SuppressWarnings("unchecked")
	public static <T> T byMap(Class<T> type, Map<String, Object> map) {
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(type); // 获取类属性
			Object obj = type.newInstance(); // 创建 JavaBean 对象
			// 给 JavaBean 对象的属性赋值
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			for (int i = 0; i < propertyDescriptors.length; i++) {
				PropertyDescriptor descriptor = propertyDescriptors[i];
				String propertyName = descriptor.getName();

				if (map.containsKey(propertyName)) {
					Method method = descriptor.getWriteMethod();
					if (method == null)
						continue;
					// 下面一句可以 try 起来，这样当一个属性赋值失败的时候就不会影响其他属性赋值。
					Object value = map.get(propertyName);
					Object[] args = new Object[1];
					args[0] = value;
					method.invoke(obj, args);
				}
			}
			return (T) obj;
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException | IntrospectionException
				| InstantiationException e) {
			log.error("byMap异常:map:[{}] type:[{}]", map, type.getName(), e);
		}
		return null;
	}
}

/**
 * 时间
 * 
 *
 */
class MyTimestamp implements Converter {

	@SuppressWarnings({ "unchecked", "hiding" })
	@Override
	public <Timestamp> Timestamp convert(Class<Timestamp> paramClass, Object paramObject) {
		LocalDateTime localDateTime = Dates.parse(paramObject.toString());
		if (localDateTime == null)
			return null;
		return (Timestamp) Dates.toTimestamp(localDateTime);
	}

}

class MyDate implements Converter {

	@SuppressWarnings({ "unchecked", "hiding" })
	@Override
	public <Date> Date convert(Class<Date> paramClass, Object paramObject) {
		LocalDateTime localDateTime = Dates.parse(paramObject.toString());
		if (localDateTime == null)
			return null;
		return (Date) Dates.toSqlDate(localDateTime);
	}

}
