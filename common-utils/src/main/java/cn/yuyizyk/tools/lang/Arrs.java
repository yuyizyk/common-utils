package cn.yuyizyk.tools.lang;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


import cn.yuyizyk.tools.clz.ReflectUtils;
import cn.yuyizyk.tools.common.Assert;
import cn.yuyizyk.tools.model.EnumerationIterator;
import cn.yuyizyk.tools.model.IteratorEnumeration;
import cn.yuyizyk.tools.serialization.BeanUtils;

/**
 * 
 * @author yuyi
 *
 */
public class Arrs {

	/**
	 * 对象是否为数组对象
	 * 
	 * @param obj
	 *            对象
	 * @return 是否为数组对象，如果为{@code null} 返回false
	 */
	public static boolean isArray(Object obj) {
		if (null == obj) {
			return false;
		}
		return obj.getClass().isArray();
	}

	/**
	 * 强转数组类型<br>
	 * 强制转换的前提是数组元素类型可被强制转换<br>
	 * 强制转换后会生成一个新数组
	 * 
	 * @param type
	 *            数组类型或数组元素类型
	 * @param arrayObj
	 *            原数组
	 * @return 转换后的数组类型
	 * @throws NullPointerException
	 *             提供参数为空
	 * @throws IllegalArgumentException
	 *             参数arrayObj不是数组
	 * @since 3.0.6
	 */
	public static Object[] cast(Class<?> type, Object arrayObj) throws NullPointerException, IllegalArgumentException {
		if (null == arrayObj) {
			throw new NullPointerException("Argument [arrayObj] is null !");
		}
		if (false == arrayObj.getClass().isArray()) {
			throw new IllegalArgumentException("Argument [arrayObj] is not array !");
		}
		if (null == type) {
			return (Object[]) arrayObj;
		}

		final Class<?> componentType = type.isArray() ? type.getComponentType() : type;
		final Object[] array = (Object[]) arrayObj;
		final Object[] result = (Object[]) Array.newInstance(componentType, array.length);
		System.arraycopy(array, 0, result, 0, array.length);
		return result;
	}

	/**
	 * 并集 A∪B<br/>
	 * 
	 * @param list
	 *            a 集
	 * @param lists
	 *            B 集
	 * 
	 * @return
	 */
	public <T extends Object> List<T> union(List<T> list1, @SuppressWarnings("unchecked") List<T>... lists) {
		Stream.of(lists).forEach(list1::addAll);
		return list1.stream().distinct().collect(Collectors.toList());
	}

	/**
	 * 差集
	 * 
	 * @param list
	 *            a 集
	 * @param lists
	 *            被减
	 * @return 差
	 */
	public <T extends Object> List<T> difference(List<T> list, @SuppressWarnings("unchecked") List<T>... lists) {
		for (List<T> l : lists) {
			if (list.isEmpty())
				return list;
			List<T> teml = list;
			list = l.stream().filter(t -> !teml.contains(t)).collect(Collectors.toList());
		}
		return list;
	}

	/**
	 * 交集 A∩B<br/>
	 * 
	 * @param list
	 *            a 集
	 * @param lists
	 *            B 集
	 * 
	 * @return
	 */
	public <T extends Object> List<T> intersection(List<T> list1, @SuppressWarnings("unchecked") List<T>... lists) {
		for (List<T> l : lists) {
			if (list1.isEmpty())
				return list1;
			List<T> teml = list1;
			list1 = l.stream().filter(t -> teml.contains(t)).collect(Collectors.toList());
		}
		return list1;
	}

	public static void main(String[] args) {
		List<List<String>> list = new ArrayList<List<String>>();
		List<String> listSub1 = new ArrayList<String>();
		List<String> listSub2 = new ArrayList<String>();
		List<String> listSub3 = new ArrayList<String>();
		listSub1.add("1");
		listSub1.add("2");

		listSub2.add("3");
		listSub2.add("4");

		listSub3.add("a");
		listSub3.add("b");

		list.add(listSub1);
		list.add(listSub2);
		list.add(listSub3);
		List<List<String>> result = new ArrayList<List<String>>();
		descartes(list, result, 0, new ArrayList<String>());

		System.out.println(result);
	}

	/**
	 * Discription:笛卡尔乘积算法 把一个List{[1,2],[3,4],[a,b]}转化成
	 * List{[1,3,a],[1,3,b],[1,4,a],[1,4,b],[2,3,a],[2,3,b],[2,4,a],[2,4,b]}数组输出
	 * </p>
	 * 
	 * @param dimvalue原List
	 * @param result通过乘积转化后的数组
	 * @param layer
	 *            中间参数
	 * @param curList
	 *            中间参数
	 */
	public static void descartes(List<List<String>> dimvalue, List<List<String>> result, int layer,
			List<String> curList) {
		if (layer < dimvalue.size() - 1) {
			if (dimvalue.get(layer).size() == 0) {
				descartes(dimvalue, result, layer + 1, curList);
			} else {
				for (int i = 0; i < dimvalue.get(layer).size(); i++) {
					List<String> list = new ArrayList<String>(curList);
					list.add(dimvalue.get(layer).get(i));
					descartes(dimvalue, result, layer + 1, list);
				}
			}
		} else if (layer == dimvalue.size() - 1) {
			if (dimvalue.get(layer).size() == 0) {
				result.add(curList);
			} else {
				for (int i = 0; i < dimvalue.get(layer).size(); i++) {
					List<String> list = new ArrayList<String>(curList);
					list.add(dimvalue.get(layer).get(i));
					result.add(list);
				}
			}
		}
	}

	/**
	 * 数组或集合转String
	 * 
	 * @param obj
	 *            集合或数组对象
	 * @return 数组字符串，与集合转字符串格式相同
	 */
	public static String toString(Object obj) {
		if (null == obj) {
			return null;
		}
		if (isArray(obj)) {
			try {
				return Arrays.deepToString((Object[]) obj);
			} catch (Exception e) {
				final String className = obj.getClass().getComponentType().getName();
				switch (className) {
				case "long":
					return Arrays.toString((long[]) obj);
				case "int":
					return Arrays.toString((int[]) obj);
				case "short":
					return Arrays.toString((short[]) obj);
				case "char":
					return Arrays.toString((char[]) obj);
				case "byte":
					return Arrays.toString((byte[]) obj);
				case "boolean":
					return Arrays.toString((boolean[]) obj);
				case "float":
					return Arrays.toString((float[]) obj);
				case "double":
					return Arrays.toString((double[]) obj);
				default:
					throw e;
				}
			}
		}
		return obj.toString();
	}

	/**
	 * 根据集合返回一个元素计数的 {@link Map}<br>
	 * 所谓元素计数就是假如这个集合中某个元素出现了n次，那将这个元素做为key，n做为value(indexs)<br>
	 * 例如：[a,b,c,c,c] 得到：<br>
	 * a: [0]<br>
	 * b: [1]<br>
	 * c: [2,3,4]<br>
	 * 
	 * @param <T>
	 *            集合元素类型
	 * @param collection
	 *            集合
	 * @return {@link Map}
	 * @see IterUtil#countMap(Iterable)
	 */
	public static <T> Map<T, Set<Integer>> countMap(Iterable<T> collection) {
		Assert.isNull(collection);
		Map<T, Set<Integer>> map = new HashMap<>();
		Iterator<T> it = collection.iterator();
		T t;
		int i = 0;
		while (it.hasNext()) {
			t = it.next();
			if (!map.containsKey(t)) {
				map.put(t, new HashSet<>());
			}
			map.get(t).add(i++);
		}
		return map;
	}

	/**
	 * 根据集合返回一个元素计数的 {@link Map}<br>
	 * 所谓元素计数就是假如这个集合中某个元素出现了n次，那将这个元素做为key，n做为value(indexs)<br>
	 * 例如：[a,b,c,c,c] 得到：<br>
	 * a: [0]<br>
	 * b: [1]<br>
	 * c: [2,3,4]<br>
	 * 
	 * @param <T>
	 *            集合元素类型
	 * @param collection
	 *            集合
	 * @return {@link Map}
	 * @see IterUtil#countMap(Iterable)
	 */
	public static <T> Map<T, Set<Integer>> countMap(T[] collection) {
		Assert.isNull(collection);
		Map<T, Set<Integer>> map = new HashMap<>();
		int i = 0;
		for (T t : collection) {
			if (!map.containsKey(t)) {
				map.put(t, new HashSet<>());
			}
			map.get(t).add(i++);
		}
		return map;
	}

	/**
	 * Iterator转换为Enumeration
	 * <p>
	 * Adapt the specified <code>Iterator</code> to the <code>Enumeration</code>
	 * interface.
	 * 
	 * @param <E>
	 *            集合元素类型
	 * @param iter
	 *            {@link Iterator}
	 * @return {@link Enumeration}
	 */
	public static <E> Enumeration<E> asEnumeration(Iterator<E> iter) {
		return new IteratorEnumeration<E>(iter);
	}

	/**
	 * Enumeration转换为Iterator
	 * <p>
	 * Adapt the specified <code>Enumeration</code> to the <code>Iterator</code>
	 * interface
	 * 
	 * @param <E>
	 *            集合元素类型
	 * @param e
	 *            {@link Enumeration}
	 * @return {@link Iterator}
	 * @see IterUtil#asIterator(Enumeration)
	 */
	public static <E> Iterator<E> asIterator(Enumeration<E> e) {
		return new EnumerationIterator<E>(e);
	}

	/**
	 * {@link Iterable}转为{@link Collection}<br>
	 * 首先尝试强转，强转失败则构建一个新的{@link ArrayList}
	 * 
	 * @param <E>
	 *            集合元素类型
	 * @param iterable
	 *            {@link Iterable}
	 * @return {@link Collection} 或者 {@link ArrayList}
	 * @since 3.0.9
	 */
	public static <E> Collection<E> toCollection(Iterable<E> iterable) {
		if (iterable instanceof Collection)
			return (Collection<E>) iterable;
		Collection<E> c = new ArrayList<E>();
		iterable.forEach(c::add);
		return c;
	}

	/**
	 * 行转列，合并相同的键，值合并为列表<br>
	 * 将Map列表中相同key的值组成列表做为Map的value<br>
	 * 是{@link #toMapList(Map)}的逆方法<br>
	 * 比如传入数据：
	 * 
	 * <pre>
	 * [
	 *  {a: 1, b: 1, c: 1}
	 *  {a: 2, b: 2}
	 *  {a: 3, b: 3}
	 *  {a: 4}
	 * ]
	 * </pre>
	 * 
	 * 结果是：
	 * 
	 * <pre>
	 * {
	 *   a: [1,2,3,4]
	 *   b: [1,2,3,]
	 *   c: [1]
	 * }
	 * </pre>
	 * 
	 * @param <K>
	 *            键类型
	 * @param <V>
	 *            值类型
	 * @param mapList
	 *            Map列表
	 * @return Map
	 * @see Maps#toListMap(Iterable)
	 */
	public static <K, V> Map<K, List<V>> toListMap(Iterable<? extends Map<K, V>> mapList) {
		return Maps.toListMap(mapList);
	}

	/**
	 * 列转行。将Map中值列表分别按照其位置与key组成新的map。<br>
	 * 是{@link #toListMap(Iterable)}的逆方法<br>
	 * 比如传入数据：
	 * 
	 * <pre>
	 * {
	 *   a: [1,2,3,4]
	 *   b: [1,2,3,]
	 *   c: [1]
	 * }
	 * </pre>
	 * 
	 * 结果是：
	 * 
	 * <pre>
	 * [
	 *  {a: 1, b: 1, c: 1}
	 *  {a: 2, b: 2}
	 *  {a: 3, b: 3}
	 *  {a: 4}
	 * ]
	 * </pre>
	 * 
	 * @param <K>
	 *            键类型
	 * @param <V>
	 *            值类型
	 * @param listMap
	 *            列表Map
	 * @return Map列表
	 * @see Maps#toMapList(Map)
	 */
	public static <K, V> List<Map<K, V>> toMapList(Map<K, ? extends Iterable<V>> listMap) {
		return Maps.toMapList(listMap);
	}

	/**
	 * 分组，按照{@link Hash}接口定义的hash算法，集合中的元素放入hash值对应的子列表中
	 * 
	 * @param collection
	 *            被分组的集合
	 * @param hash
	 *            默认Hash值算法，决定元素放在第几个分组的规则
	 * @return 分组后的集合
	 */
	public static <T> List<List<T>> group(Collection<T> collection, Function<T, Integer> hash) {
		final List<List<T>> result = new ArrayList<>();
		if (Objs.isEmpty(collection)) {
			return result;
		}
		int index;
		List<T> subList;
		for (T t : collection) {
			index = hash.apply(t);
			if (result.size() - 1 < index) {
				while (result.size() - 1 < index) {
					result.add(null);
				}
				subList = new ArrayList<>();
				subList.add(t);
				result.set(index, subList);
			} else {
				subList = result.get(index);
				if (null == subList) {
					subList = new ArrayList<>();
					subList.add(t);
					result.set(index, subList);
				} else {
					subList.add(t);
				}
			}
		}

		return result;
	}

	/**
	 * 根据元素的指定字段名分组，非Bean都放在第一个分组中
	 * 
	 * @param collection
	 *            集合
	 * @param fieldName
	 *            元素Bean中的字段名，非Bean都放在第一个分组中
	 * @return 分组列表
	 */
	public static <T> List<List<T>> groupByField(Collection<T> collection, final String fieldName) {
		List<Object> fieldNameList = new ArrayList<>();
		return group(collection, t -> {
			if (null == t || false == BeanUtils.isBean(t.getClass())) {
				// 非Bean放在同一子分组中
				return 0;
			}
			final Object value = ReflectUtils.getFieldValue(t, fieldName);
			int hash = fieldNameList.indexOf(value);
			if (hash < 0) {
				fieldNameList.add(value);
				return fieldNameList.size() - 1;
			} else {
				return hash;
			}
		});
	}
}
