package com.gezhonglei.commons.xml;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gezhonglei.commons.util.ConverterUtil;
import com.gezhonglei.commons.xml.annotation.XmlIgnore;
import com.gezhonglei.commons.xml.annotation.XmlMap;
import com.gezhonglei.commons.xml.annotation.XmlProp;
import com.gezhonglei.commons.xml.annotation.XmlTag;

public class XmlSerializer {
	
	private static Logger logger = LoggerFactory.getLogger(XmlSerializer.class);

	private String header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
	
	private boolean ignoreNull = true;
	
	private String nullValue = "";
	
	private static final String[] ANGLE_BRACKET = new String[] {"<", "/", ">"};
	private static final String EQUAL = "=";
	private static final String WHITESPACE = " ";
	private static final String QUOTE = "\"";
	
	public XmlSerializer() {
	}

	public String serialize(Object object) {
		StringBuilder sbStr = new StringBuilder();
		if(object != null) 
		{
			doClass(object, sbStr, null);
		}
		return sbStr.toString();
	}

	private void doClass(Object object, StringBuilder sbStr, String tagName) {
		Class<?> clazz = object.getClass();
		XmlTag tag = clazz.getAnnotation(XmlTag.class);
		tagName = tagName != null ? tagName : tag != null ? tag.name() : clazz.getSimpleName();
		sbStr.append(ANGLE_BRACKET[0] + tagName);
		doProperties(object, sbStr);
		sbStr.append(ANGLE_BRACKET[2]);
		doSubTag(object, sbStr);
		appendTagEnd(sbStr, tagName);
	}

	private void doProperties(Object object, StringBuilder sbStr) {
		Node node = getNode(object.getClass());
		String[] props = node.getProps();
		for (String prop : props) {
			Field field = node.getPropField(prop);
			if(field != null) {
				try {
					Object value = field.get(object);
					value = value != null ? value : !ignoreNull ? nullValue : null;
					if(value != null) {
						appendAttribute(sbStr, prop, value);
					}
				} catch (IllegalArgumentException | IllegalAccessException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
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

	private void doSubTag(Object object, StringBuilder sbStr) {
		Node node = getNode(object.getClass());
		String[] subTags = node.getTags();
		for (String tag : subTags) {
			Field field = node.getTagField(tag);
			if(field != null) {
				try {
					Object value = field.get(object);
					if(value != null) {
						Class<?> clazz = value.getClass();
						if(ConverterUtil.isConvertable(clazz)) {
							appendTag(sbStr, tag, value);
						} else if(clazz.isArray()) {
							appendTagBegin(sbStr, tag);
							Object[] array = (Object[]) value;
							for (Object subVal : array) {
								doClass(subVal, sbStr, null);
							}
							appendTagEnd(sbStr, tag);
						} else if(List.class.isAssignableFrom(clazz)) {
							appendTagBegin(sbStr, tag);
							List<?> list = (List<?>) value;
							for (Object subVal : list) {
								doClass(subVal, sbStr, null);
							}
							appendTagEnd(sbStr, tag);
						} else if(Map.class.isAssignableFrom(clazz)) {
							XmlMap fieldTag = field.getAnnotation(XmlMap.class);
							boolean isItemVisible = true, isKeyTag = false, isValueTag = false;
							String itemTagName = "item", keyName = "key", valueName = "value";	
							
							if(fieldTag != null) {
								tag = ifEmpty(fieldTag.name(), tag);
								itemTagName = ifEmpty(fieldTag.itemTag(), itemTagName);
								valueName = fieldTag.valueName();
								isItemVisible = fieldTag.isItemVisible();
								isKeyTag = fieldTag.isKeyTag();
								isValueTag = fieldTag.isValueTag();
								
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
								
								appendTagBegin(sbStr, tag);
								Map<?,?> map = (Map<?,?>) value;
								for (Entry<?, ?> entry : map.entrySet()) {
									if(isItemVisible) {
										sbStr.append(ANGLE_BRACKET[0] + itemTagName);
										if(!isKeyTag) {
											appendAttribute(sbStr, keyName, entry.getKey());
											if(!isValueTag) {
												appendAttribute(sbStr, valueName, entry.getValue());
											}
										}
										sbStr.append(ANGLE_BRACKET[2]);
										if(isKeyTag) {
											if(!isValueTag) {
												sbStr.append(ANGLE_BRACKET[0] + entry.getKey());
												appendAttribute(sbStr, valueName, entry.getValue());
												sbStr.append(ANGLE_BRACKET[2]);
												appendTagEnd(sbStr, entry.getKey() + "");
											} else {
												appendTagBegin(sbStr, entry.getKey() + "");
												appendTag(sbStr, valueName, entry.getValue());
												appendTagEnd(sbStr, entry.getKey() + "");
											}
										}
										if(!isKeyTag && isValueTag) {
											appendTag(sbStr, valueName, entry.getValue());
										}
										appendTagEnd(sbStr, itemTagName);
									} else {
										if(!isValueTag) {
											sbStr.append(ANGLE_BRACKET[0] + entry.getKey());
											appendAttribute(sbStr, valueName, entry.getValue());
											sbStr.append(ANGLE_BRACKET[2]);
											appendTagEnd(sbStr, entry.getKey() + "");
										} else {
											appendTagBegin(sbStr, entry.getKey() + "");
											appendTag(sbStr, valueName, entry.getValue());
											appendTagEnd(sbStr, entry.getKey() + "");
										}
									}
									
								}
								appendTagEnd(sbStr, tag);
							}
						} else {
							doClass(value, sbStr, tag);
						}
					}
				} catch (IllegalArgumentException | IllegalAccessException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
	}
	
	private String ifEmpty(String str, String defval) {
		return !isEmpty(str) ? str : defval;
	}

	private static boolean isEmpty(String str) {
		return str == null || "".equals(str);
	}
	
	private static void appendTagBegin(StringBuilder sbStr, String tagName) {
		sbStr.append(ANGLE_BRACKET[0] + tagName + ANGLE_BRACKET[2]);
	}
	
	private static void appendTagEnd(StringBuilder sbStr, String tagName) {
		sbStr.append(ANGLE_BRACKET[0] + ANGLE_BRACKET[1] + tagName + ANGLE_BRACKET[2]);
	}
	
	private static void appendTag(StringBuilder sbStr, String tagName, Object value) {
		boolean isEmpty = isEmpty(tagName);
		if(!isEmpty) {
			sbStr.append(ANGLE_BRACKET[0] + tagName + ANGLE_BRACKET[2]);
		}
		sbStr.append(value);
		if(!isEmpty) {
			appendTagEnd(sbStr, tagName);
		}
	}
	
	private static void appendAttribute(StringBuilder sbStr, String propName, Object value) {
		sbStr.append(WHITESPACE + propName + EQUAL + QUOTE + value + QUOTE);
	}
	
	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

}
