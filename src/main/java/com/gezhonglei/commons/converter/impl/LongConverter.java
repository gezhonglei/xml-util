package com.gezhonglei.commons.converter.impl;

import com.gezhonglei.commons.converter.Converter;

public class LongConverter implements Converter {

	@Override
	public Object getValue(String str) {
		return Long.valueOf(str);
	}

}
