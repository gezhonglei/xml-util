package com.gezhonglei.commons.xml.converter;

import java.util.HashMap;
import java.util.Map;

public class ConverterFactory {

	private static Map<String, Converter> converterMap = new HashMap<String, Converter>();

	static {
		Converter converter = doubleConverter();
		converterMap.put(double.class.getName(), converter);
		converterMap.put(Double.class.getName(), converter);
		
		converter = floatConverter();
		converterMap.put(float.class.getName(), converter);
		converterMap.put(Float.class.getName(), converter);
		
		converter = longConverter();
		converterMap.put(long.class.getName(), converter);
		converterMap.put(Long.class.getName(), converter);
		
		converter = intConverter();
		converterMap.put(int.class.getName(), converter);
		converterMap.put(Integer.class.getName(), converter);
		
		converter = shortConverter();
		converterMap.put(short.class.getName(), converter);
		converterMap.put(Short.class.getName(), converter);
		
		converter = byteConverter();
		converterMap.put(byte.class.getName(), converter);
		converterMap.put(Byte.class.getName(), converter);
		
		converter = charConverter();
		converterMap.put(char.class.getName(), converter);
		converterMap.put(Character.class.getName(), converter);
		
		converter = booleanConverter();
		converterMap.put(boolean.class.getName(), converter);
		converterMap.put(Boolean.class.getName(), converter);
		
		converterMap.put(String.class.getName(), new Converter() {
			@Override
			public Object getValue(String str) {
				return str;
			}
		});
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
	
	public static boolean isConvertable(Class<?> clazz) {
		return converterMap.containsKey(clazz.getName());
	}
	
	private static Converter charConverter() {
		return new Converter() {
			@Override
			public Object getValue(String str) {
				return str.length() > 0 ? str.charAt(0) : (char)0; 
			}
		};
	}

	public static void put(Class<?> clazz, Converter converter) {
		converterMap.put(clazz.getName(), converter);
	}

	private static Converter longConverter() {
		return new Converter() {
			@Override
			public Object getValue(String str) {
				return Long.valueOf(str);
			}
		};
	}

	private static Converter shortConverter() {
		return new Converter() {
			@Override
			public Object getValue(String str) {
				return Short.valueOf(str);
			}
		};
	}

	private static Converter byteConverter() {
		return new Converter() {
			@Override
			public Object getValue(String str) {
				return Byte.valueOf(str);
			}
		};
	}

	private static Converter booleanConverter() {
		return new Converter() {
			@Override
			public Object getValue(String str) {
				return Boolean.valueOf(str);
			}
		};
	}

	private static Converter intConverter() {
		return new Converter() {
			@Override
			public Object getValue(String str) {
				return Integer.valueOf(str);
			}
		};
	}

	private static Converter floatConverter() {
		return new Converter() {
			@Override
			public Object getValue(String str) {
				return Float.valueOf(str);
			}
		};
	}

	private static Converter doubleConverter() {
		return new Converter() {
			@Override
			public Object getValue(String str) {
				return Double.valueOf(str);
			}
		};
	}
}
