package com.example.ExampleApiDemo.exceptions;

import lombok.Data;

@Data
public class GeminiException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private String message;

	public GeminiException(String message) {
		this.message = message;
	}

}
