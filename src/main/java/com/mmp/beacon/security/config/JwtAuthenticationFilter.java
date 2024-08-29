package com.mmp.beacon.security.config;

import com.mmp.beacon.security.application.CustomUserDetails;
import com.mmp.beacon.security.application.UserDetailService;
import com.mmp.beacon.security.provider.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import io.jsonwebtoken.ExpiredJwtException;


import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailService userDetailService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        log.debug("Processing request URI: {}", requestURI);

        // 리프레시 엔드포인트를 처리하지 않도록 설정
        if ("/api/v1/refresh".equals(requestURI)) {
            log.debug("Bypassing JWT filter for refresh endpoint.");
            chain.doFilter(request, response);
            return;
        }

        final String requestTokenHeader = request.getHeader("Authorization");

        String username = null;
        String jwtToken = null;

        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                username = jwtTokenProvider.getUsernameFromToken(jwtToken);
                log.debug("Extracted username from JWT: {}", username);
            } catch (ExpiredJwtException e) {
                log.warn("JWT Token has expired, redirecting to refresh token flow.");
                // Refresh 토큰 플로우로 넘기는 로직 추가
                chain.doFilter(request, response);
                return;
            } catch (Exception e) {
                log.error("Error extracting JWT token", e);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT Token has expired");
                return;
            }
        } else {
            log.warn("JWT token does not start with Bearer String: {}", requestTokenHeader);
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailService.loadUserByUsername(username);
            if (jwtTokenProvider.validateToken(jwtToken)) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

                CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
                log.debug("Set authentication in security context for: {}", customUserDetails.getUsername());
            }
        }

        chain.doFilter(request, response);
    }
}