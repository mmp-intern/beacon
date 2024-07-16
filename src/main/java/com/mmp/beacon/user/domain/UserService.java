package com.mmp.beacon.user.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public AbstractUser findByUserId(String userId) {
        return userRepository.findByUserId(userId);
    }

    public boolean validateUser(AbstractUser user, String password) {
        return user != null && user.getPassword().equals(password);
    }

    public String getUserRole(AbstractUser user) {
        return user.getRole().name();
    }
}
