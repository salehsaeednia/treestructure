package com.tradeshift.codechallenge.saleh.exception;


public abstract class BaseException extends RuntimeException {

	private final ResultError error;


	public BaseException(ResultError error) {
		super(error.toString());
		this.error = error;
	}

	public BaseException(ResultError error, Throwable cause) {
		super(error.toString(), cause);
		this.error = error;
	}



	public BaseException(ResultError error, Throwable cause, boolean enableSuppression,
						 boolean writableStackTrace) {
		super(error.toString(), cause, enableSuppression, writableStackTrace);
		this.error = error;
	}

	public ResultError getError() {
		return error;
	}
}
