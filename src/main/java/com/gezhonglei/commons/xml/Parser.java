package com.gezhonglei.commons.xml;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gezhonglei.commons.converter.ConverterNotFoundException;
import com.gezhonglei.commons.util.ConverterUtil;
import com.gezhonglei.commons.util.ReflectUtil;
import com.gezhonglei.commons.xml.annotation.EmptyClass;
import com.gezhonglei.commons.xml.annotation.XmlIgnore;
import com.gezhonglei.commons.xml.annotation.XmlMap;
import com.gezhonglei.commons.xml.annotation.XmlProp;
import com.gezhonglei.commons.xml.annotation.XmlTag;

/**
 * Xml解析类
 * <p>根据Class类型解析实例，注意事项</p>
 * <p>
 * <ul>
 * <li><b>循环引用</b>：类之间不要出现循环引用，如果存在使用@XmlIgnore忽略</li>
 * <li><b>适合自定义配置</b>：因xml过于灵活，在使用XPath表达式才能解析的场景，不适合用本组件</li>
 * <li><b>复杂类型</b>：字段类型，支持基本类型、实体类型、Map和List类型，不支持Set类型。</li>
 * <li><b>泛型使用</b>：使用泛型时，一定要在注解的属性中指定泛型参数类型，除非默认参数匹配。因为Java类型擦除，无法通过反射获取泛型参数</li>
 * <li><b>大小写</b>：默认大小写敏感</li>
 * </ul>
 * </p>
 *
 * <p>设计方案</p>
 * <p>
 * <ul>
 * <li>放弃使用XPath查询表达式实现配置。原因是用XPath解析灵活、强大，但是不适合用作Xml序列化。</li>
 * <li>考虑Xml序列化与反序列化配置一致性。基于上注解配置的在序列化与序列化都适用。</li>
 * <li>Exception分级：开发抛出异常太多，根据需求可归类分组，并使用自定义异常来简化异常。</li>
 * <li>Xml转义处理</li>
 * </ul>
 * </p>
 */
public class Parser {
	
	private static final Logger logger = LoggerFactory.getLogger(XmlUtil.class); 
	
	/**
	 * 从指定xml元素开始解析出指定Class类型的实例对象
	 * @param element xml元素
	 * @param clazz 类型
	 * @return clazz类型的实例对象
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ConverterNotFoundException
	 * @throws BaseException 
	 */
	public static <T> T parse(Element element, Class<T> clazz) throws  ConverterNotFoundException, BaseException {
		return parse(element, clazz, null, false);
	}
	
	/**
	 * 从指定Xml元素中解析出对应类型的实例对象
	 * @param element xml元素
	 * @param clazz 类型
	 * @param name xml元素名称
	 * @return clazz类型的实例对象
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ConverterNotFoundException
	 * @throws BaseException 
	 */
	public static <T> T parse(Element element, Class<T> clazz, String name, boolean ignoreCase) throws ConverterNotFoundException, BaseException {
		if(clazz == null) return null;
		if(ConverterUtil.isConvertable(clazz)) {
			return ConverterUtil.getValue(element.getTextTrim(), clazz);
		}
		
		XmlTag tag = clazz.getAnnotation(XmlTag.class);
		String tagName = StringUtils.isNotEmpty(name) ? name : tag != null ? tag.name() : clazz.getSimpleName();
		ignoreCase = tag != null ? tag.ignoreCase() : ignoreCase;
		if(!isEquals(tagName, element.getName(), ignoreCase)) {
			return null;
		}
		// Class必须有无参构造函数
		T object = null;
		try {
			object = clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			logger.error(e.getMessage(), e);
			throw new ClassInstanceException(e.getMessage(), e);
		}
		Node node = getNode(clazz);
		parseAttributes(object, node, element, ignoreCase);
		parseSubTag(object, node, element, ignoreCase);
		return object;
	}
	
	private static void parseAttributes(Object obj, Node node, Element element, boolean ignoreCase) {
		String valuestr = null;
		Field field = null;
		XmlProp xmlProp = null;
		for (String prop : node.getProps()) {
			field = node.getPropField(prop);
			xmlProp = field.getAnnotation(XmlProp.class);
			ignoreCase = xmlProp != null ? xmlProp.ignoreCase() : ignoreCase;
			// element.attribute(prop) 无法忽略大小写
			valuestr = getAttributeValue(element, prop, ignoreCase, null);
			if(valuestr != null) {
				try {
					Object value = ConverterUtil.getValue(valuestr, field.getType());
					field.set(obj, value);
				} catch (ConverterNotFoundException e) {
					logger.error(e.getMessage(), e);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					logger.error(e.getMessage(), e);
				} 
			}
		}
	}
	
	private static void parseSubTag(Object obj, Node node, Element element, boolean ignoreCase) throws ConverterNotFoundException, BaseException {
		Element el = null;
		Field field = null;
		Class<?> fieldType = null;
		Object fieldValue = null;
		String valuestr = null;
		
		for (String tagName : node.getTags()) {
			// element.element(tagName)
			el = getSubElement(element, tagName, ignoreCase);
			field = node.getTagField(tagName);
			fieldType = field.getType();

			try {
				if(ConverterUtil.isConvertable(fieldType)) {
					valuestr = el.getTextTrim();
					fieldValue = ConverterUtil.getValue(valuestr, fieldType);
				} else if(fieldType.isArray()) {
					fieldValue = parseArray(el, fieldType, ignoreCase);
				} else if(List.class.isAssignableFrom(fieldType)) {
					fieldValue = parseList(el, field, ignoreCase, field.get(obj));
				} else if(Map.class.isAssignableFrom(fieldType)) {
					fieldValue = parseMap(el, field, ignoreCase, field.get(obj));
				} else {
					fieldValue = parse(el, fieldType, tagName, ignoreCase);
				}
				field.set(obj, fieldValue);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				logger.error(e.getMessage(), e);
				throw new FieldReadWriteException(e.getMessage(), e);
			}
		}
	}
		
	private static Object parseArray(Element el, Class<?> fieldType,boolean ignoreCase) throws ConverterNotFoundException, BaseException {
		XmlTag fieldTag = fieldType.getAnnotation(XmlTag.class);
		Class<?> subType = fieldType.getComponentType();					
		
		// subTag: 首选Field上的XmlTag，其次是子元素Class上的XmlTag，最后是ClassName
		String subTag = fieldTag != null ? fieldTag.subName() : null;
		if(StringUtils.isEmpty(subTag)) {
			XmlTag subTypeTag = subType.getAnnotation(XmlTag.class);
			if(subTypeTag != null && !StringUtils.isEmpty(subTypeTag.name())) {
				subTag = subTypeTag.name();
			} else {
				subTag = subType.getSimpleName();
			}
		}
		
		ignoreCase = fieldTag != null ? fieldTag.ignoreCase() : ignoreCase;
		List<Object> arr = new ArrayList<Object>();
		Element subel = null;
		Object subValue = null;
		for (Object item : el.elements()) {
			subel = (Element)item;
			if(isEquals(subel.getName(), subTag, ignoreCase)) {
				if((subValue = parse(subel, subType, null, ignoreCase)) != null) {
					arr.add(subValue);
				}
			}
		}
		return arr.toArray(ReflectUtil.getArray(subType, 0));
	}
	
	private static Object parseList(Element el, Field field, boolean ignoreCase, Object value) throws ConverterNotFoundException, BaseException {
		XmlTag fieldTag = field.getAnnotation(XmlTag.class);
		Class<?> subType = fieldTag != null ? fieldTag.subClass() : null;
		// 不指定类型参数没有办法解析
		if(subType == null || subType.equals(EmptyClass.class)) {
			return null;
		}
		
		// subTag: 首选Field上的XmlTag，其次是子元素Class上的XmlTag，最后是ClassName
		String subTag = fieldTag != null ? fieldTag.subName() : null;
		if(StringUtils.isEmpty(subTag)) {
			XmlTag subTypeTag = subType.getAnnotation(XmlTag.class);
			if(subTypeTag != null && !StringUtils.isEmpty(subTypeTag.name())) {
				subTag = subTypeTag.name();
			} else {
				subTag = subType.getSimpleName();
			}
		}
		
		ignoreCase = fieldTag != null ? fieldTag.ignoreCase() : ignoreCase;
		List<Object> arr = new ArrayList<Object>();
		Element subel = null;
		Object subValue = null;
		for (Object item : el.elements()) {
			subel = (Element)item;
			if(isEquals(subel.getName(), subTag, ignoreCase)) {
				if((subValue = parse(subel, subType, null, ignoreCase)) != null) {
					arr.add(subValue);
				}
			}
		}
		
		if(value != null) {
			@SuppressWarnings("unchecked")
			List<Object> fieldValue = (List<Object>) value;
			fieldValue.addAll(arr);
		} else {
			value = arr;
		}
		return value;
	}
	
	@SuppressWarnings("unchecked")
	private static Object parseMap(Element el, Field field, boolean ignoreCase, Object value) throws ConverterNotFoundException {
		String fieldTagName = field.getName();
		boolean isItemVisible = true, isKeyTag = false, isValueTag = false;
		String itemTagName = "item", keyName = "key", valueName = "value";	
		Class<?> keyType = String.class, valType = String.class;
		
		XmlMap fieldTag = field.getAnnotation(XmlMap.class);
		if(fieldTag != null) {
			keyType = fieldTag.keyType();
			valType = fieldTag.valueType();
			fieldTagName = ifEmpty(fieldTag.name(), fieldTagName);
			itemTagName = ifEmpty(fieldTag.itemTag(), itemTagName);
			//keyName = fieldTag.keyName();
			valueName = fieldTag.valueName();
			isItemVisible = fieldTag.isItemVisible();
			isKeyTag = fieldTag.isKeyTag();
			isValueTag = fieldTag.isValueTag();
			
			// 如果valueName取content，那么keyName必须非空(即key不可为content)
			if(StringUtils.isEmpty(valueName)) {
				isValueTag = true;
				keyName = ifEmpty(fieldTag.keyName(), keyName);
			}
			// 如果key是属性，不允许空
			if(!isKeyTag) {
				keyName = ifEmpty(fieldTag.keyName(), keyName);
			}
			
			// 调整isItemVisible
			isItemVisible = isItemVisible || (!isKeyTag && !isValueTag);
		}
		
		ignoreCase = fieldTag != null ? fieldTag.ignoreCase() : ignoreCase;
		List<?> list = null;
		Object key = null, val = null;
		Map<Object,Object> map = new HashMap<Object, Object>();
		for (Object item : el.elements()) {
			Element curEl = (Element)item;
			if(isItemVisible) {
				if(!isEquals(curEl.getName(), itemTagName, ignoreCase)) continue;
				
				curEl = isKeyTag ? ((list = curEl.elements()).size() > 0 ? (Element)list.get(0) : null) : curEl;
			}
			if(curEl != null) {
				key = isKeyTag ? curEl.getName() : getAttributeValue(curEl, keyName, ignoreCase, null);
				if(key == null) {
					continue;
				}
				
				val = StringUtils.isEmpty(valueName) ? curEl.getTextTrim() : 
					isValueTag ? ((curEl = getSubElement(curEl, valueName, ignoreCase)) != null ? curEl.getTextTrim() : null) : 
						getAttributeValue(curEl, valueName, ignoreCase, null);
				if(val == null) {
					continue;
				}
				key = ConverterUtil.getValue(key.toString(), keyType);
				val = ConverterUtil.getValue(val.toString(), valType);
				map.put(key, val);
			}
		}
		if(value != null) {
			((Map<Object, Object>)value).putAll(map);
		} else {
			value = map;
		}
		return value;
	}
	
	private static Map<Class<?>, Node> nodeMap = new HashMap<Class<?>, Node>();
	
	private static Node getNode(Class<?> clazz) {
		Node node = nodeMap.get(clazz);
		if(node == null) {
			node = new Node();
			nodeMap.put(clazz, node);
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				if(!field.isAccessible()) {
					field.setAccessible(true);
				}
				XmlIgnore ignoreAnnotation = field.getAnnotation(XmlIgnore.class);
				if(ignoreAnnotation != null) {
					continue;
				}
				XmlTag tag = field.getAnnotation(XmlTag.class);
				if(tag != null) {
					node.addTag(tag, field);
					continue;
				}
				XmlProp prop = field.getAnnotation(XmlProp.class);
				if(prop != null) {
					node.addProp(prop, field);
					continue;
				}
				XmlMap mapTag = field.getAnnotation(XmlMap.class);
				if(mapTag != null) {
					node.addTag(mapTag.name(), field);
					continue;
				}
				// 默认模式：ignore
				// node.addProp(field.getName(), field);
			}
		}
		return node;
	}
	
	private static boolean isEquals(String tagName, String name,
			boolean ignoreCase) {
		return (!ignoreCase && tagName.equals(name)) || (ignoreCase && tagName.equalsIgnoreCase(name));
	}

	private static String ifEmpty(String str, String defval) {
		return !StringUtils.isEmpty(str) ? str : defval;
	}
	
	/**
	 * 找到第一个满足指定Tag名称的元素
	 * @param el 元素
	 * @param name Tag名称
	 * @param ignoreCase 忽略大小写
	 * @return 返回满足条件的Element，如没有满足条件的，返回null。
	 */
	private static Element getSubElement(Element el, String name, boolean ignoreCase) {
		Element subEl = null;
		for (Object obj : el.elements()) {
			subEl = (Element)obj;
			if(isEquals(subEl.getName(), name, ignoreCase)) {
				return subEl;
			}
		}
		return null;
	}
	
	/**
	 * 获取元素指定属性名称的值，如不存在，返回默认值
	 */
	private static String getAttributeValue(Element el, String attrName, boolean ignoreCase, String defValue) {
		Attribute attr = null;
		for (Object obj : el.attributes()) {
			attr = (Attribute)obj;
			if(isEquals(attr.getName(), attrName, ignoreCase)) {
				return attr.getValue();
			}
		}
		return defValue;
	}
	
}
