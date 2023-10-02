package com.user.test.response;

import java.util.Set;

import javax.validation.constraints.NotBlank;

import com.user.test.enums.Authority;
import com.user.test.model.AuthorityModel;

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
	private Set<AuthorityModel> authorities;

}
