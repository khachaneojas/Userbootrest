package com.user.test.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenValidationResponse {

	 private boolean isDefault;
	 private boolean isAdmin;
	 private boolean isSales;
	 private boolean isNone;
	 Integer userId;
}
