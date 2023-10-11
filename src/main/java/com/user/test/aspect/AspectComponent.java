package com.user.test.aspect;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.user.test.exception.InvalidDataException;
import com.user.test.response.TokenValidationResponse;
import com.user.test.service.UserService;

@Aspect
@Component
public class AspectComponent {
	
	@Autowired
	private UserService userService;
	
	@Before(value = "executing(* com.user.test.controller.UserController.getUserIdTest(..))")
	public void isValidTokenBefore(JoinPoint joinPoint) {
		System.out.println("Before Executed");
		
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if(null == requestAttributes)
			throw new InvalidDataException("No request in the context");
			
		HttpServletRequest request = requestAttributes.getRequest();
		String authorizationHeaderString = request.getHeader(HttpHeaders.AUTHORIZATION);
		
		if(null == authorizationHeaderString)
			throw new InvalidDataException("Authorization Header is required");
		
		TokenValidationResponse validationResponse = userService.isTokenValid(authorizationHeaderString);
		Object[] args = joinPoint.getArgs();
		
		for(Object arg : args) {
			if(arg instanceof TokenValidationResponse) {
				
				TokenValidationResponse existing = (TokenValidationResponse) arg;
				existing.setUserId(validationResponse.getUserId());
				existing.setAdmin(validationResponse.isAdmin());
				existing.setSales(validationResponse.isSales());
				existing.setDefault(validationResponse.isDefault());
				existing.setNone(validationResponse.isNone());
				break;
			}
		}
		
	}
	
	@After(value = "executing(* com.user.test.controller.UserController.getUserIdTest(..))")
	public void isValidTokenAfter(JoinPoint joinPoint) {
		System.out.println("After Executed");
	}
	
	@Around(value = "executing(* com.user.test.controller.UserController.getUserIdTest(..))")
	public void isValidTokenAfter(ProceedingJoinPoint joinPoint) {
		System.out.println("Before executed");
		
		try {
			joinPoint.proceed();
		}catch(Throwable e) {
			e.printStackTrace();
		}
		
		System.out.println("After Executed");
	}
	
}
