package com.mmp.beacon.user.exception;

import com.mmp.beacon.global.exception.BeaconException;
import com.mmp.beacon.global.exception.ErrorCode;

import static org.springframework.http.HttpStatus.NOT_FOUND;

public class UserNotFoundException extends BeaconException {

    public UserNotFoundException() {
        super(new ErrorCode(NOT_FOUND, "사용자를 찾을 수 없습니다."));
    }

    public UserNotFoundException(String message) {
        super(new ErrorCode(NOT_FOUND, message));
    }
}
