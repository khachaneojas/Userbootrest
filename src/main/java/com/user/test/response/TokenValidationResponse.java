package com.user.test.response;

import lombok.Data;

@Data
public class TokenValidationResponse {

	 private boolean isDefault;
	 private boolean isAdmin;
	 private boolean isSales;
	 private boolean isNone;
	 Integer userId;
}
