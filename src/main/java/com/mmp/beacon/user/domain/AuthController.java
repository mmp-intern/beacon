package com.mmp.beacon.user.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public String login(@RequestParam String userId, @RequestParam String password) {
        AbstractUser user = userService.findByUserId(userId);
        if (userService.validateUser(user, password)) {
            return "로그인 성공: " + userService.getUserRole(user);
        } else {
            return "로그인 실패";
        }
    }
}
