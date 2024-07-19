package com.mmp.beacon.Security;

import com.mmp.beacon.user.domain.AbstractUser;
import com.mmp.beacon.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AbstractUser abstractUser = userRepository.findByUserId(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new UserDetail(abstractUser);
    }
}
