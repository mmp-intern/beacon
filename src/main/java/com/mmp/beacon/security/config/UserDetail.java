package com.mmp.beacon.security.config;

import com.mmp.beacon.user.domain.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;

@Getter
public class UserDetail implements UserDetails {
    private final String userId;
    private final String password;
    private final String email;
    private final String position;
    private final String name;
    private final String phone;
    private final String company;
    private final String role;
    private final User user;

    public UserDetail(User user) {
        this.userId = user.getUserId();
        this.password = user.getPassword();
        this.email = user.getEmail();
        this.position = user.getPosition();
        this.role = user.getRole().name();
        this.name = user.getName();
        this.phone = user.getPhone();
        this.company = user.getCompany().getName();
        this.user = user;

        // 로그 추가
        Logger logger = LoggerFactory.getLogger(UserDetail.class);
        logger.info("UserDetail Constructed: email={}, position={}", this.email, this.position);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public User getUser() {
        return user;
    }
}
