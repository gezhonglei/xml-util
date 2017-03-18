package com.gezhonglei.commons.converter.impl;

import com.gezhonglei.commons.converter.Converter;

public class ByteConverter implements Converter {

	@Override
	public Object getValue(String str) {
		return Byte.valueOf(str);
	}

}
