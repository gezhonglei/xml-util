package com.gezhonglei.commons.converter.impl;

import com.gezhonglei.commons.converter.Converter;

public class CharConverter implements Converter {

	@Override
	public Object getValue(String str) {
		return str.length() > 0 ? str.charAt(0) : (char)0;
	}

}
