package com.fastdfs.common;

@SuppressWarnings("serial")
public class CustomException extends Exception {
	public CustomException() {
	}

	public CustomException(String message) {
		super(message);
	}
}