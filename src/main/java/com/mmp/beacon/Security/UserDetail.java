package com.mmp.beacon.Security;

import com.mmp.beacon.user.domain.AbstractUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserDetail implements UserDetails {
    private final AbstractUser abstractUser;

    public UserDetail(AbstractUser abstractUser) {
        this.abstractUser = abstractUser;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> abstractUser.getRole().name());
    }

    @Override
    public String getPassword() {
        return abstractUser.getPassword();
    }

    @Override
    public String getUsername() {
        return abstractUser.getUserId();
    }

    public Long getUserId() {
        return abstractUser.getId();
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
