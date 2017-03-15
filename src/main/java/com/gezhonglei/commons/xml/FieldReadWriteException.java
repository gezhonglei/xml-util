package com.gezhonglei.commons.xml;

public class FieldReadWriteException extends BaseException {

	private static final long serialVersionUID = -5143627039964261553L;
	public FieldReadWriteException() {
		super();
	}

	public FieldReadWriteException(String msg) {
		super(msg);
	}

	public FieldReadWriteException(String msg, Throwable t) {
		super(msg, t);
	}
}
