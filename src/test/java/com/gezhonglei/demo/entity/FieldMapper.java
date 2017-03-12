package com.gezhonglei.demo.entity;

import com.gezhonglei.commons.xml.annotation.XmlProp;
import com.gezhonglei.commons.xml.annotation.XmlTag;

@XmlTag(name = "attr")
public class FieldMapper {
	@XmlProp("name")
	private String fieldName;
	@XmlProp(name = "toname")
	private String toFieldName;
	@XmlProp
	private String type;

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getToFieldName() {
		return toFieldName;
	}

	public void setToFieldName(String toFieldName) {
		this.toFieldName = toFieldName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
