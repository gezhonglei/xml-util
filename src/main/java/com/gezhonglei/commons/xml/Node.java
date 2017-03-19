package com.gezhonglei.commons.xml;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.gezhonglei.commons.xml.annotation.XmlProp;
import com.gezhonglei.commons.xml.annotation.XmlTag;

class Node {
	
	private List<String> props = new ArrayList<String>();
	private List<String> tags = new ArrayList<String>();
	private Map<String, Field> propFieldMap = new HashMap<String, Field>();
	private Map<String, Field> tagFieldMap = new HashMap<String, Field>();

	public void addTag(XmlTag tag, Field field) {
		String name = ifEmpty(tag.value(), tag.name());
		addTag(name, field);
	}
	
	public void addTag(String tagName, Field field) {
		String name = ifEmpty(tagName, field.getName());
		if(!tags.contains(name)) {
			tags.add(name);
		}
		tagFieldMap.put(name, field);
	}

	public void addProp(XmlProp prop, Field field) {
		String name = ifEmpty(prop.value(), prop.name());
		addProp(name, field);
	}
	
	public void addProp(String propName, Field field) {
		String name = ifEmpty(propName, field.getName());
		if(!props.contains(name)) {
			props.add(name);
		}
		propFieldMap.put(name, field);
	}
	
	private String ifEmpty(String str, String defValue) {
		return !StringUtils.isEmpty(str) ? str : defValue;
	}
	
	public Field getTagField(String name) {
		return tagFieldMap.get(name);
	}
	
	public Field getPropField(String name, boolean ignoreCase) {
		if(!ignoreCase) {
			return propFieldMap.get(name);
		}
		for (String key : propFieldMap.keySet()) {
			if(key.equalsIgnoreCase(name)) {
				return propFieldMap.get(key);
			}
		}
		return null;
	}
	
	public Field getPropField(String name) {
		return this.getPropField(name, false);
	}

	public String[] getTags() {
		return tags.toArray(new String[0]); 
		//return tagFieldMap.keySet().toArray(new String[0]);
	}

	public String[] getProps() {
		return props.toArray(new String[0]); 
		//return propFieldMap.keySet().toArray(new String[0]);
	}
}
