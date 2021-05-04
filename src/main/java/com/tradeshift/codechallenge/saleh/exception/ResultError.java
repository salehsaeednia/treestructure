package com.tradeshift.codechallenge.saleh.exception;

public enum ResultError {
	Success(0, "Success"),
	InvalidNode(-100, "Invalid Node"),
	InvalidParent(-101, "Invalid Parent"),
	RootAlreadyExist(-102, "Root Already Exist"),
	InvalidArgument(-103, "Invalid Argument"),
	InternalError(-500, "Internal Error"),;
	private final Integer code;
	private final String description;

	ResultError(Integer code, String description) {
		this.code = code;
		this.description = description;
	}

	public Integer getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public String toString() {
		return
				"code=" + code +
				", description='" + description;
	}
}
