package com.mmp.beacon.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mmp.beacon.security.handler.JwtAuthenticationFailureHandler;
import com.mmp.beacon.security.handler.JwtAuthenticationSuccessHandler;
import com.mmp.beacon.security.provider.JwtAuthenticationProvider;
import com.mmp.beacon.security.provider.JwtTokenProvider;
import com.mmp.beacon.user.domain.repository.AbstractUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final ObjectMapper mapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AbstractUserRepository abstractUserRepository;

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(config -> config.sessionCreationPolicy(STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/v1/login").permitAll()
                        .requestMatchers("/api/v1/admin/**").hasAnyAuthority("SUPER_ADMIN")
                        .requestMatchers("/api/v1/superadmin/**").hasAuthority("SUPER_ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/users/**").hasAnyAuthority("SUPER_ADMIN", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/profile").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/**").hasAnyAuthority("SUPER_ADMIN", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/users/**").hasAnyAuthority("SUPER_ADMIN", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/users/**").hasAnyAuthority("SUPER_ADMIN", "ADMIN")
                        .anyRequest().authenticated());

        http.addFilterBefore(loginProcessingFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    JwtLoginProcessingFilter loginProcessingFilter() {
        JwtLoginProcessingFilter loginFilter = new JwtLoginProcessingFilter(mapper);
        loginFilter.setAuthenticationManager(authenticationManager());
        loginFilter.setAuthenticationSuccessHandler(successHandler());
        loginFilter.setAuthenticationFailureHandler(failureHandler());
        return loginFilter;
    }

    @Bean
    AuthenticationManager authenticationManager() {
        List<AuthenticationProvider> authenticationProviders = List.of(
                new JwtAuthenticationProvider(abstractUserRepository, (BCryptPasswordEncoder) passwordEncoder())
        );
        return new ProviderManager(authenticationProviders);
    }

    @Bean
    AuthenticationSuccessHandler successHandler() {
        return new JwtAuthenticationSuccessHandler(jwtTokenProvider, mapper);
    }

    @Bean
    AuthenticationFailureHandler failureHandler() {
        return new JwtAuthenticationFailureHandler(mapper);
    }
}
