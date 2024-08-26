package com.mmp.beacon.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mmp.beacon.security.handler.JwtAuthenticationFailureHandler;
import com.mmp.beacon.security.handler.JwtAuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
public class JwtLoginProcessingFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper;

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
    }

    public void setAuthenticationSuccessHandler(JwtAuthenticationSuccessHandler successHandler) {
        super.setAuthenticationSuccessHandler(successHandler);
    }

    public void setAuthenticationFailureHandler(JwtAuthenticationFailureHandler failureHandler) {
        super.setAuthenticationFailureHandler(failureHandler);
    }
}
