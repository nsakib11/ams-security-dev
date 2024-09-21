package com.ams.api.exception;

import com.ams.api.model.CustomError;

import java.util.List;

public class GlobalException extends NGIBaseException {

	private static final long serialVersionUID = 619665828707923005L;

	public GlobalException() {
		super();
	}

	public GlobalException(List<CustomError> error) {
		super(error);
	}

	public GlobalException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public GlobalException(String message, Throwable cause) {
		super(message, cause);
	}

	public GlobalException(String message) {
		super(message);
	}

	public GlobalException(Throwable cause) {
		super(cause);
	}
}