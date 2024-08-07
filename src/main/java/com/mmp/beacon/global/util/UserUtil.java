package com.mmp.beacon.global.util;

import com.mmp.beacon.security.application.CustomUserDetails;
import com.mmp.beacon.user.exception.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * 사용자 유틸리티 클래스
 * 현재 인증된 사용자 ID를 반환하는 메서드를 제공합니다.
 */
@Component
public class UserUtil {

    /**
     * 현재 인증된 사용자 ID를 반환합니다.
     *
     * @return 현재 인증된 사용자 ID
     * @throws UnauthorizedException 인증되지 않은 경우 예외를 던집니다.
     */
    public static Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetail = (CustomUserDetails) auth.getPrincipal();
            return userDetail.getUser().getId();
        }
        throw new UnauthorizedException();
    }
}
