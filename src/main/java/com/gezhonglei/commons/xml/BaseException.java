package com.gezhonglei.commons.xml;

public abstract class BaseException extends Exception {
	private static final long serialVersionUID = 8458427888135372883L;

	public BaseException() {
		super();
	}

	public BaseException(String msg) {
		super(msg);
	}

	public BaseException(String msg, Throwable t) {
		super(msg, t);
	}
}
