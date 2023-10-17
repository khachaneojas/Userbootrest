package com.user.test.exception;

public class UnauthorizedAccessException extends RuntimeException{



	public UnauthorizedAccessException() {
		super();
	}

	public UnauthorizedAccessException(String message) {
		super(message);
//		log.error(message);
	}

}
