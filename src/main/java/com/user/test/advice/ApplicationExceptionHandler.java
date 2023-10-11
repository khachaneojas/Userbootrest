package com.user.test.advice;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.user.test.exception.AccountDisabledException;
import com.user.test.exception.InvalidDataException;
import com.user.test.exception.UnauthorizedAccessException;

import io.jsonwebtoken.JwtException;


@RestControllerAdvice
public class ApplicationExceptionHandler {
	
//	@ResponseStatus(HttpStatus.BAD_REQUEST)
//	@ExceptionHandler(MethodArgumentNotValidException.class)
//	public Map<String, String> handleInvalidArgument(MethodArgumentNotValidException ex){
//		Map<String, String> errorMap = new HashMap<>();
//		ex.getBindingResult().getFieldErrors().forEach(error -> {
//			errorMap.put(error.getField(), error.getDefaultMessage());
//		});
//		return errorMap;
//	}
	
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public Map<String, String> handleInvalidArgument(MethodArgumentNotValidException ex){
		Map<String, String> errorMap = new HashMap<String, String>();
		FieldError fieldError = ex.getBindingResult().getFieldErrors().get(0);
		errorMap.put("error", fieldError.getDefaultMessage());
		return errorMap;
	}
	
	
	@ResponseStatus(HttpStatus.FORBIDDEN)
	@ExceptionHandler(MissingRequestHeaderException.class)
	public Map<String, String> handleMissingRequestHeaderException(MissingRequestHeaderException ex){
		Map<String, String> errorMap = new HashMap<String, String>();
		errorMap.put("error", ex.getMessage());
		return errorMap;
	}
	
	@ResponseStatus(HttpStatus.FORBIDDEN)
	@ExceptionHandler(JwtException.class)
	public Map<String, String> handleJwtException(JwtException ex){
		Map<String, String> errorMap = new HashMap<String, String>();
		errorMap.put("error", ex.getMessage());
		return errorMap;
	}
	
//Custom Exception
	@ResponseStatus(HttpStatus.FORBIDDEN)
	@ExceptionHandler(AccountDisabledException.class)
	public Map<String, String> handleAccountDisabledException(AccountDisabledException ex){
		Map<String, String> errorMap = new HashMap<String, String>();
		errorMap.put("error", ex.getMessage());
		return errorMap;
	}
	
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(InvalidDataException.class)
	public Map<String, String> handleInvalidDataException(InvalidDataException ex){
		Map<String, String> errorMap = new HashMap<String, String>();
		errorMap.put("error", ex.getMessage());
		return errorMap;
	}
	
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	@ExceptionHandler(UnauthorizedAccessException.class)
	public Map<String, String> handleUnauthorizedAccessException(UnauthorizedAccessException ex){
		Map<String, String> errorMap = new HashMap<String, String>();
		errorMap.put("error", ex.getMessage());
		return errorMap;
	}
	
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(UsernameNotFoundException.class)
	public Map<String, String> handleUsernameNotFoundException(UsernameNotFoundException ex){
		Map<String, String> errorMap = new HashMap<String, String>();
		errorMap.put("error", ex.getMessage());
		return errorMap;
	}
	
}
