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

    // @RequiredArgsConstructor로 인해 ObjectMapper를 받는 생성자가 자동으로 생성됩니다.

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
