package com.user.test.payload;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class RegisterUser {
	
	@NotBlank(message = "firstname cannot be blank")
	private String firstname;

	private String lastname;

	@NotBlank(message = "email cannot be blank")
	@Email
	private String email;
	
	@NotBlank(message = "username cannot be blank")
	private String username;
	
	@NotBlank(message = "password cannot be blank")
	private String password;

}
