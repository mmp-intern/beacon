package com.mmp.beacon.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mmp.beacon.security.application.CustomUserDetails;
import com.mmp.beacon.security.provider.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class JwtAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        // 인증이 성공하면 JWT 토큰을 생성
        String token = jwtTokenProvider.generateToken((CustomUserDetails) authentication.getPrincipal());

        // 생성된 토큰을 맵에 담아 JSON 형태로 변환
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);

        // 응답에 JSON 형태로 변환된 토큰을 씀
        response.getWriter().write(objectMapper.writeValueAsString(tokenMap));
    }
}
