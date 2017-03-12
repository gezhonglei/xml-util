package com.gezhonglei.demo.entity;

import com.gezhonglei.commons.xml.annotation.XmlTag;

@XmlTag(name = "access")
public class AccessResource {
	@XmlTag(name = "resourceCollectionName")
	private String collectionName;
	@XmlTag
	private String resourceName;
	@XmlTag
	private FieldMapper[] fields;

	public String getCollectionName() {
		return collectionName;
	}

	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public FieldMapper[] getFields() {
		return fields;
	}

	public void setFields(FieldMapper[] fields) {
		this.fields = fields;
	}
}
