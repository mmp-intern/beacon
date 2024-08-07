package com.mmp.beacon.security.application;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderUtil {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "asdf1"; // 슈퍼 관리자의 비밀번호
        String encodedPassword = encoder.encode(rawPassword);
        System.out.println(encodedPassword);
    }
}
