package com.mmp.beacon.user.exception;

import com.mmp.beacon.global.exception.BeaconException;
import com.mmp.beacon.global.exception.ErrorCode;

import static org.springframework.http.HttpStatus.FORBIDDEN;

public class SuperAdminAccessException extends BeaconException {

    public SuperAdminAccessException() {
        super(new ErrorCode(FORBIDDEN, "슈퍼 어드민은 접근할 수 없습니다."));
    }

    public SuperAdminAccessException(String message) {
        super(new ErrorCode(FORBIDDEN, message));
    }
}
