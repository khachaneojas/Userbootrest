package com.user.test.payload;

import javax.validation.constraints.NotBlank;

import com.user.test.enums.Authority;
import com.user.test.model.AuthorityModel;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateAuthority {
	@NotBlank
	private String username_or_email;
	
	private Long authorityID;
	
	private Boolean enabled;
}
