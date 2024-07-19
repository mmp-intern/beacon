package com.mmp.beacon.Security;

import com.mmp.beacon.company.domain.Company;
import com.mmp.beacon.user.domain.User;
import com.mmp.beacon.user.domain.UserRepository;
import com.mmp.beacon.Security.SecurityUtil;
import com.mmp.beacon.Security.UserRegisterDTO;
import com.mmp.beacon.Security.UserLoginDTO;
import com.mmp.beacon.user.domain.AbstractUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void register(UserRegisterDTO userDto, Company company) {
        String encPassword = bCryptPasswordEncoder.encode(userDto.getPassword());
        User user = new User(userDto.getUserId(), encPassword, userDto.getName(), userDto.getPhone(), userDto.getEmail(), userDto.getSex(), userDto.getPosition(), company);
        userRepository.save(user);
    }

    public boolean authenticate(UserLoginDTO userDto) {
        Optional<AbstractUser> userOpt = userRepository.findByUserId(userDto.getUserId());

        return userOpt.map(user -> bCryptPasswordEncoder.matches(userDto.getPassword(), user.getPassword())).orElse(false);
    }

    public Optional<AbstractUser> getProfile() {
        Long userId = SecurityUtil.getCurrentUserId();
        return userRepository.findById(userId);
    }

    public Optional<AbstractUser> getProfileById(Long userId) {
        return userRepository.findById(userId);
    }

}