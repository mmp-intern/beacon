package com.mmp.beacon.auth;

import com.mmp.beacon.user.domain.User;
import com.mmp.beacon.user.domain.repository.UserRepository;
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
        User User = userRepository.findByUserId(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new UserDetail(User);
    }
}


