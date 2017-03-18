package com.gezhonglei.commons.converter.impl;

import com.gezhonglei.commons.converter.Converter;

public class FloatConverter implements Converter {

	@Override
	public Object getValue(String str) {
		return Float.valueOf(str);
	}

}
