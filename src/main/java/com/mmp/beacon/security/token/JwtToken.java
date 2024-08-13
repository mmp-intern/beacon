package com.mmp.beacon.security.token;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JwtToken {

	private String accessToken;

	@Builder
    private JwtToken(String accessToken) {
        this.accessToken = accessToken;
    }
}