package com.gezhonglei.commons.converter.impl;

import com.gezhonglei.commons.converter.Converter;

public class BooleanConverter implements Converter {

	@Override
	public Object getValue(String str) {
		return Boolean.valueOf(str);
	}

}
