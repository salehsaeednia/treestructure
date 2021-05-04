package com.tradeshift.codechallenge.saleh.web.rest;


import com.tradeshift.codechallenge.saleh.exception.ResultError;

public class RestResult<T> {
	private int errorCode;
	private String errorDescription;
	private T data;

	public RestResult() {
	}

	public RestResult(int errorCode, String errorDescription, T data) {
		this.errorCode = errorCode;
		this.errorDescription = errorDescription;
		this.data = data;
	}

	public RestResult(ResultError error, T data) {
		this.errorCode = error.getCode();
		this.errorDescription = error.getDescription();
		this.data = data;
	}

	// region <Getters - Setters>


	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorDescription() {
		return errorDescription;
	}

	public void setErrorDescription(String errorDescription) {
		this.errorDescription = errorDescription;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	// endregion
}
