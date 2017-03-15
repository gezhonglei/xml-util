package com.gezhonglei.commons.util;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gezhonglei.commons.converter.Converter;
import com.gezhonglei.commons.converter.ConverterNotFoundException;
import com.gezhonglei.commons.converter.impl.*;

public class ConverterUtil {
	
	private static Logger logger = LoggerFactory.getLogger(ConverterUtil.class);

	private static Map<String, Converter> converterMap = new HashMap<String, Converter>();

	static {
		Converter converter = new DoubleConverter();
		converterMap.put(double.class.getName(), converter);
		converterMap.put(Double.class.getName(), converter);
		
		converter = new FloatConverter();
		converterMap.put(float.class.getName(), converter);
		converterMap.put(Float.class.getName(), converter);
		
		converter = new LongConverter();
		converterMap.put(long.class.getName(), converter);
		converterMap.put(Long.class.getName(), converter);
		
		converter = new IntegerConverter();
		converterMap.put(int.class.getName(), converter);
		converterMap.put(Integer.class.getName(), converter);
		
		converter = new ShortConverter();
		converterMap.put(short.class.getName(), converter);
		converterMap.put(Short.class.getName(), converter);
		
		converter = new ByteConverter();
		converterMap.put(byte.class.getName(), converter);
		converterMap.put(Byte.class.getName(), converter);
		
		converter = new CharConverter();
		converterMap.put(char.class.getName(), converter);
		converterMap.put(Character.class.getName(), converter);
		
		converter = new BooleanConverter();
		converterMap.put(boolean.class.getName(), converter);
		converterMap.put(Boolean.class.getName(), converter);
		
		converterMap.put(String.class.getName(), new StringConverter());
	}

	@SuppressWarnings("unchecked")
	public static <T> T getValue(String str, Class<T> clazz)
			throws ConverterNotFoundException {
		Converter converter = converterMap.get(clazz.getName());
		if (converter != null) {
			return (T) converter.getValue(str);
		} else if(clazz.isEnum()) {			
			@SuppressWarnings("rawtypes")
			final Class<? extends Enum> enumType = (Class<? extends Enum>) clazz;
			return (T) Enum.valueOf(enumType, str);
		}
		throw new ConverterNotFoundException("Error happend when parsing: " + str);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getValue(String str, T defval) {
		try {
			return (T) getValue(str, defval.getClass());
		} catch (ConverterNotFoundException e) {
			logger.error(e.getMessage(), e);
		}
		return defval;
	}
	
	public static boolean isConvertable(Class<?> clazz) {
		return clazz.isEnum() || converterMap.containsKey(clazz.getName());
	}

	public static void registConverter(Class<?> clazz, Converter converter) {
		converterMap.put(clazz.getName(), converter);
	}
}
