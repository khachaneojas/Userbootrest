package com.user.test.payload;

import javax.validation.constraints.NotBlank;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateUser {

	private Integer id;
	
	@NotBlank(message = "firstname cannot be blank")
	private String firstname;

	private String lastname;

	@NotBlank(message = "email cannot be blank")
	private String email;
	
	@NotBlank(message = "username cannot be blank")
	private String username;
	
	@NotBlank(message = "password cannot be blank")
	private String password;
}
