package org.springside.examples.bootapi.service.exception;

public class ServiceException extends RuntimeException {

	/**
	 * 类似于 uuid
	 */
	private static final long serialVersionUID = -8634700792767837033L;

	public ErrorCode errorCode;

	/**
	 * 构造函数， super 的作用是？super可以理解为是指向自己超（父）类对象的一个指针，而这个超类指的是离自己最近的一个父类。
	 * 最后找到了 Throwable 头上
	 * @param message
	 * @param errorCode
	 */
	public ServiceException(String message, ErrorCode errorCode) {
		super(message);
		this.errorCode = errorCode;
	}
}
