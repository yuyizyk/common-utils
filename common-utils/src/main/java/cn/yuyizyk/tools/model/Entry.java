package cn.yuyizyk.tools.model;

@SuppressWarnings("rawtypes")
public class Entry<K, V> implements java.util.Map.Entry {

	private K key;
	private V value;

	public void setKey(K key) {
		this.key = key;
	}

	@Override
	public Object getKey() {
		return key;
	}

	@Override
	public Object getValue() {
		return this.value;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object setValue(Object value) {
		this.value = (V) value;
		return this.value;
	}

}
