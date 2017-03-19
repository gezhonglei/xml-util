package com.gezhonglei.demo.test;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.gezhonglei.commons.util.ReflectUtil;
import com.gezhonglei.commons.xml.XmlUtil;
import com.gezhonglei.commons.xml.annotation.XmlMap;
import com.gezhonglei.demo.entity.MapCollection;

public class APITest {
	@Test
	public void testPath() {
		System.out.println(XmlUtil.class.getResource("."));
		System.out.println(XmlUtil.class.getResource(".").getPath());
		System.out.println(XmlUtil.class.getResource("/"));
		System.out.println(XmlUtil.class.getClassLoader().getResource("."));
		System.out.println(XmlUtil.class.getClassLoader().getResource("/"));
		
		File file = new File(XmlUtil.class.getResource(".").getFile());
		System.out.println(file.getPath());
	}
	
	public void testXmlDom() {
		
	}
	
	@Test
	public void testEnum() {
		System.out.println(Enum.class);
		System.out.println(HttpMethod.class);
		System.out.println(HttpMethod.GET.name());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testArrayObject() throws NoSuchFieldException, SecurityException, InstantiationException, IllegalAccessException {
		Class<?> clazz = TestArray.class;
		Object obj = clazz.newInstance();
		Field f = clazz.getDeclaredField("strs");
		Class<?> ftype = f.getType();
		if(ftype.isArray()) {
			//Object value = ftype.newInstance();	// 数组不能直接初始化		
			Class<?> subType = ftype.getComponentType();
			@SuppressWarnings("rawtypes")
			ArrayList list = new ArrayList();
			if(!subType.isPrimitive()) {
				list.add(subType.newInstance());
			}
			
			Object value = list.toArray(ReflectUtil.getArray(subType, 0));
			
			if(!f.isAccessible()) {
				f.setAccessible(true);
			}
			f.set(obj, value);
		}
	}
	
	@Test
	public void testTestList() throws NoSuchFieldException, SecurityException {
		ArrayList<String> list = new ArrayList<String>();
		ParameterizedType paramType = (ParameterizedType) list.getClass().getGenericSuperclass();
		System.out.println(paramType);	// java.util.AbstractList<E>
		System.out.println(paramType.getActualTypeArguments()[0]); // E
		
		paramType = (ParameterizedType) TestArray2.class.getGenericSuperclass();
		System.out.println(paramType);	// java.util.ArrayList<java.lang.String>
		System.out.println(paramType.getActualTypeArguments()[0]); // class java.lang.String
		
		// 只有继承关系写法：UserDao extends JdbcSuport<User> 直接获取代码字节中的类型User
	}
	
	@Test
	public void testList() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		ArrayList<Object> arr = new ArrayList<Object>();
		arr.add("Test");
		TestArray testArr = new TestArray();
		testArr.list = new ArrayList<Integer>();
		Field f = TestArray.class.getDeclaredField("list");
		@SuppressWarnings("unchecked")
		List<Object> fvalue = (List<Object>)f.get(testArr);
		fvalue.addAll(arr);
		System.out.println(fvalue);
		f.set(testArr, arr);	// 未报错
		// 总结1： List、 List<?> 与 List<Object> 三者之间相互转换是没有问题的 
		
		List<Object> strArr = new ArrayList<Object>();
		strArr.add("a");
		// Field strField = TestArray.class.getDeclaredField("strs");
		//strField.set(testArr, strArr.toArray()); //报错，同下例
		// String[] strArr2 = (String[]) new Object[] { "aaa", "bbb" }; // 报错
		
		//Object[] objects = new String[] {"aaa", "bbb", "ccc"};	// 正常
		Field objField = TestArray.class.getDeclaredField("objs");
		objField.set(testArr, new String[] {"aaa", "bbb"});
		
		// 总结2：Object[]与String[]之间的转换是不可逆转的
		Object aa = new ArrayList<String>();
		@SuppressWarnings("unchecked")
		List<Object> intList = (List<Object>) aa;
		intList.add(123);
		intList.add("123a");
		System.out.println(intList);
	}
	
	@Test
	public void testAssignable() throws NoSuchFieldException, SecurityException {
		System.out.println(List.class.isAssignableFrom(ArrayList.class));
		System.out.println(ArrayList.class.isAssignableFrom(List.class));
		
		Class<?> clazz = MapCollection.class;
		System.out.println(clazz.getDeclaredField("map1").getAnnotation(XmlMap.class));
	}
	
	@Test
	public void testInstanceGenericInterface() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		TestArray obj = new TestArray();
		Field f = TestArray.class.getDeclaredField("list");
		List<Object> arr = new ArrayList<Object>();
		arr.add("aaaa");
		arr.add(2);
		f.set(obj, arr);
		System.out.println(obj.list);
		System.out.println(obj.list.get(0));
		//Integer v = obj.list.get(0);
		//System.out.println(v);
		
		// 以上现象很奇怪！Java泛型的类型擦除可以解释这一点
	}
}

enum HttpMethod {
	GET,
	POST,
	PUT,
	DELETE
}

class TestArray {
	int[] ints;
	String[] strs;
	Object[] objs;
	List<Integer> list;
}

@SuppressWarnings("serial")
class TestArray2 extends ArrayList<String> {
	
}