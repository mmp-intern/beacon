package com.mmp.beacon.user.exception;

import com.mmp.beacon.global.exception.BeaconException;
import com.mmp.beacon.global.exception.ErrorCode;

import static org.springframework.http.HttpStatus.FORBIDDEN;

public class UserWithoutPermissionException extends BeaconException {

    public UserWithoutPermissionException() {
        super(new ErrorCode(FORBIDDEN, "관리자 또는 슈퍼 어드민만 접근할 수 있습니다."));
    }

    public UserWithoutPermissionException(String message) {
        super(new ErrorCode(FORBIDDEN, message));
    }
}
