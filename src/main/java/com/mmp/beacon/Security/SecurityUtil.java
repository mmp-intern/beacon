package com.mmp.beacon.Security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

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

    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetail) {
                return ((UserDetail) principal).getUserId();
            } else if (principal instanceof String) {
                try {
                    return Long.valueOf((String) principal);
                } catch (NumberFormatException e) {
                    // principal이 String이지만 숫자가 아닐 경우 null 반환
                    return null;
                }
            }
        }
        return null;
    }
}
