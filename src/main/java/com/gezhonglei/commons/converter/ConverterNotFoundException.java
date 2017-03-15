package com.gezhonglei.commons.converter;

public class ConverterNotFoundException extends Exception {

	private static final long serialVersionUID = 4364519791181383368L;

	public ConverterNotFoundException() {
		super();
	}
	
	public ConverterNotFoundException(String msg) {
		super(msg);
	}
	
	public ConverterNotFoundException(String msg, Throwable t) {
		super(msg, t);
	}
}
