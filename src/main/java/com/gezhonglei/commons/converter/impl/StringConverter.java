package com.gezhonglei.commons.converter.impl;

import com.gezhonglei.commons.converter.Converter;

public class StringConverter implements Converter {

	@Override
	public Object getValue(String str) {
		return str;
	}

}
