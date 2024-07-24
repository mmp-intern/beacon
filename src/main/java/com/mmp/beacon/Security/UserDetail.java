package com.mmp.beacon.Security;

import com.mmp.beacon.user.domain.AbstractUser;
import com.mmp.beacon.company.domain.Company;
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
    private final String sex;
    private final String position;
    private final String name;
    private final String phone;
    private final String company;
    private final String role;

    public UserDetail(AbstractUser abstractUser) {
        this.userId = abstractUser.getUserId();
        this.password = abstractUser.getPassword();
        this.email = abstractUser.getEmail();
        this.sex = abstractUser.getSex();
        this.position = abstractUser.getPosition();
        this.role = abstractUser.getRole().name();

        // Initialize additional fields for User
        if (abstractUser instanceof User) {
            User user = (User) abstractUser;
            this.name = user.getName();
            this.phone = user.getPhone();
            this.company = user.getCompany().getName();
        } else {
            this.name = null;
            this.phone = null;
            this.company = null;
        }
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
}
