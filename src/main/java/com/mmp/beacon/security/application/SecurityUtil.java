package com.mmp.beacon.security.application;

import com.mmp.beacon.security.config.UserDetail;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    // 현재 인증된 사용자 이름 반환
    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetail) {
                return ((UserDetail) principal).getUsername();
            } else if (principal instanceof String) {
                return (String) principal;
            }
        }
        return null;
    }

    // 현재 인증된 사용자 ID 반환
    public static String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetail) {
                return ((UserDetail) principal).getUserId();
            } else if (principal instanceof String) {
                return (String) principal;
            }
        }
        return null;
    }
}

