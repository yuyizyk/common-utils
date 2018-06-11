package cn.yuyizyk.tools.function;

/**
 * 
 * @author yuyi
 * 
 * @param <T>
 */
@FunctionalInterface
public interface Action<T> {
	void apply(T t);
}
