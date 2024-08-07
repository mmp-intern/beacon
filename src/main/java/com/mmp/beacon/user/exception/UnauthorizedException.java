package com.mmp.beacon.user.exception;

import com.mmp.beacon.global.exception.BeaconException;
import com.mmp.beacon.global.exception.ErrorCode;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

/**
 * 인증되지 않은 사용자의 접근 시 던지는 예외 클래스
 */
public class UnauthorizedException extends BeaconException {

    public UnauthorizedException() {
        super(new ErrorCode(UNAUTHORIZED, "인증되지 않은 사용자입니다."));
    }

    public UnauthorizedException(String message) {
        super(new ErrorCode(UNAUTHORIZED, message));
    }
}