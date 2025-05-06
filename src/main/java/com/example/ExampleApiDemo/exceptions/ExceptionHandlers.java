package com.example.ExampleApiDemo.exceptions;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlers {

	@ExceptionHandler(GeminiException.class)
	public ResponseEntity<Map<String, Object>> handleGeminiException(GeminiException ex) {
		Map<String, Object> errorDetails = new HashMap<>();
		errorDetails.put("error", "Gemini Exception");
		errorDetails.put("message", ex.getMessage());
		errorDetails.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
		return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
