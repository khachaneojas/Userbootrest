package com.user.test.response;

import javax.validation.constraints.NotBlank;

import com.user.test.enums.Authority;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class UserResponse {
	
	private int id;
	private String firstname;
	private String lastname;
	private String email;
	private String username;
	private Authority authority;

}
