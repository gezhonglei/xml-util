package com.gezhonglei.commons.util;

import java.lang.reflect.Array;

public class ReflectUtil {
	
	/**
	 * 构建一个指定class类型、len长度的的数组实例对象
	 * @param clazz 类型
	 * @param len 长度
	 * @return 数组
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] getArray(Class<T> clazz, int len) {
		return (T[]) Array.newInstance(clazz, len);
	}
	
	/**
	 * 扩展数组
	 * @param array 数据对象
	 * @param size 指定大小
	 * @return 数组
	 */
	public Object growArray(Object array, int size) {
		if(array != null && array.getClass().isArray()) {
		    Class<?> type = array.getClass().getComponentType();
		    Object grown = Array.newInstance(type, size);
		    System.arraycopy(array, 0, grown, 0, Math.min(Array.getLength(array), size));
		    return grown;
		}
		return null;
	}
}
