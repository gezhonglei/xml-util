package com.gezhonglei.commons.converter.impl;

import com.gezhonglei.commons.converter.Converter;

public class IntegerConverter implements Converter {

	@Override
	public Object getValue(String str) {
		return Integer.valueOf(str);
	}

}
