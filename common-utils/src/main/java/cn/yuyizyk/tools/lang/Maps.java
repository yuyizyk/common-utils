package cn.yuyizyk.tools.lang;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public class Maps {

	/**
	 * 映射键值（参考Python的zip()函数）<br>
	 * 例如：<br>
	 * keys = a,b,c,d<br>
	 * values = 1,2,3,4<br>
	 * delimiter = , 则得到的Map是 {a=1, b=2, c=3, d=4}<br>
	 * 如果两个数组长度不同，则只对应最短部分
	 * 
	 * @param keys
	 *            键列表
	 * @param values
	 *            值列表
	 * @param delimiter
	 *            分隔符
	 * @param isOrder
	 *            是否有序
	 * @return Map
	 * @since 3.0.4
	 */
	public static Map<String, String> zip(String keys, String values, String delimiter, boolean isOrder) {
		return zip(Strs.split(keys, delimiter), Strs.split(values, delimiter), isOrder);
	}

	/**
	 * 映射键值（参考Python的zip()函数）<br>
	 * 例如：<br>
	 * keys = [a,b,c,d]<br>
	 * values = [1,2,3,4]<br>
	 * 则得到的Map是 {a=1, b=2, c=3, d=4}<br>
	 * 如果两个数组长度不同，则只对应最短部分
	 * 
	 * @param <K>
	 *            Key类型
	 * @param <V>
	 *            Value类型
	 * @param keys
	 *            键列表
	 * @param values
	 *            值列表
	 * @param isOrder
	 *            是否有序
	 * @return Map
	 * @since 3.0.4
	 */
	public static <K, V> Map<K, V> zip(K[] keys, V[] values, boolean isOrder) {
		if (Objs.isEmpty(keys) || Objs.isEmpty(values)) {
			return null;
		}

		final int size = Math.min(keys.length, values.length);
		final Map<K, V> map = isOrder ? new LinkedHashMap<>(size) : new HashMap<>(size);
		for (int i = 0; i < size; i++) {
			map.put(keys[i], values[i]);
		}

		return map;
	}

	/**
	 * 映射键值（参考Python的zip()函数），返回Map无序<br>
	 * 
	 * <pre>
	 * 例如：
	 * keys = a,b,c,d
	 * values = 1,2,3,4
	 * delimiter = , 则得到的Map是 {a=1, b=2, c=3, d=4}
	 * 如果两个数组长度不同，则只对应最短部分
	 * </pre>
	 * 
	 * @param keys
	 *            键列表
	 * @param values
	 *            值列表
	 * @param delimiter
	 *            分隔符
	 * @return Map
	 */
	public static Map<String, String> zip(String keys, String values, String delimiter) {
		return zip(keys, values, delimiter, false);
	}

	/**
	 * 映射键值（参考Python的zip()函数）<br>
	 * 例如：<br>
	 * keys = [a,b,c,d]<br>
	 * values = [1,2,3,4]<br>
	 * 则得到的Map是 {a=1, b=2, c=3, d=4}<br>
	 * 如果两个数组长度不同，则只对应最短部分
	 * 
	 * @param <K>
	 *            键类型
	 * @param <V>
	 *            值类型
	 * @param keys
	 *            键列表
	 * @param values
	 *            值列表
	 * @return Map
	 */
	public static <K, V> Map<K, V> zip(Collection<K> keys, Collection<V> values) {
		if (Objs.isEmpty(keys) || Objs.isEmpty(values)) {
			return null;
		}

		final List<K> keyList = new ArrayList<K>(keys);
		final List<V> valueList = new ArrayList<V>(values);

		final int size = Math.min(keys.size(), values.size());
		final Map<K, V> map = new HashMap<K, V>((int) (size / 0.75));
		for (int i = 0; i < size; i++) {
			map.put(keyList.get(i), valueList.get(i));
		}

		return map;
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
	 * @see MapUtil#toListMap(Iterable)
	 */
	@SuppressWarnings("serial")
	public static <K, V> Map<K, List<V>> toListMap(Iterable<? extends Map<K, V>> mapList) {
		final HashMap<K, List<V>> resultMap = new HashMap<>();
		if (Objs.isEmpty(mapList)) {
			return resultMap;
		}

		Set<Entry<K, V>> entrySet;
		for (Map<K, V> map : mapList) {
			entrySet = map.entrySet();
			K key;
			List<V> valueList;
			for (Entry<K, V> entry : entrySet) {
				key = entry.getKey();
				valueList = resultMap.get(key);
				if (null == valueList) {
					resultMap.put(key, new ArrayList<V>() {
						{
							add(entry.getValue());
						}
					});
				} else {
					valueList.add(entry.getValue());
				}
			}
		}

		return resultMap;
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
		final List<Map<K, V>> resultList = new ArrayList<>();
		if (Objs.isEmpty(listMap)) {
			return resultList;
		}
		boolean isEnd = true;// 是否结束。标准是元素列表已耗尽
		int index = 0;// 值索引
		Map<K, V> map;
		do {
			isEnd = true;
			map = new HashMap<>();
			List<V> vList;
			int vListSize;
			for (Entry<K, ? extends Iterable<V>> entry : listMap.entrySet()) {
				vList = new ArrayList<V>();
				entry.getValue().forEach(vList::add);
				vListSize = vList.size();
				if (index < vListSize) {
					map.put(entry.getKey(), vList.get(index));
					if (index != vListSize - 1) {
						// 当值列表中还有更多值（非最后一个），继续循环
						isEnd = false;
					}
				}
			}
			if (false == map.isEmpty()) {
				resultList.add(map);
			}
			index++;
		} while (false == isEnd);

		return resultList;
	}

	/**
	 * 从Map中获取指定键列表对应的值列表<br>
	 * 如果key在map中不存在或key对应值为null，则返回值列表对应位置的值也为null
	 * 
	 * @param <K>
	 *            键类型
	 * @param <V>
	 *            值类型
	 * @param map
	 *            {@link Map}
	 * @param keys
	 *            键列表
	 * @return 值列表
	 * @since 3.0.8
	 */
	@SuppressWarnings("unchecked")
	public static <K, V> ArrayList<V> valuesOfKeys(Map<K, V> map, K... keys) {
		final ArrayList<V> values = new ArrayList<>();
		for (K k : keys) {
			values.add(map.get(k));
		}
		return values;
	}

	/**
	 * 通过Entry排序，可以按照键排序，也可以按照值排序，亦或者两者综合排序
	 * 
	 * @param <K>
	 *            键类型
	 * @param <V>
	 *            值类型
	 * @param entryCollection
	 *            Entry集合
	 * @param comparator
	 *            {@link Comparator}
	 * @return {@link LinkedList}
	 * @since 3.0.9
	 */
	public static <K, V> LinkedHashMap<K, V> sortToMap(Collection<Map.Entry<K, V>> entryCollection,
			Comparator<Map.Entry<K, V>> comparator) {
		List<Map.Entry<K, V>> list = new LinkedList<>(entryCollection);
		Collections.sort(list, comparator);

		LinkedHashMap<K, V> result = new LinkedHashMap<>();
		for (Map.Entry<K, V> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

	/**
	 * 排序Map
	 * 
	 * @param <K>
	 *            键类型
	 * @param <V>
	 *            值类型
	 * @param map
	 *            Map
	 * @param comparator
	 *            Entry比较器
	 * @return {@link TreeMap}
	 * @since 3.0.9
	 */
	public static <K, V> TreeMap<K, V> sort(Map<K, V> map, Comparator<? super K> comparator) {
		final TreeMap<K, V> result = new TreeMap<K, V>(comparator);
		result.putAll(map);
		return result;
	}
}
