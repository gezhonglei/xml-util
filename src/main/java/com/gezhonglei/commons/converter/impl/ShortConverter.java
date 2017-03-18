package com.gezhonglei.commons.converter.impl;

import com.gezhonglei.commons.converter.Converter;

public class ShortConverter implements Converter {

	@Override
	public Object getValue(String str) {
		return Short.valueOf(str);
	}

}
