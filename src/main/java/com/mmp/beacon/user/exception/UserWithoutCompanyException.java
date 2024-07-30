package com.mmp.beacon.user.exception;

import com.mmp.beacon.global.exception.BeaconException;
import com.mmp.beacon.global.exception.ErrorCode;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class UserWithoutCompanyException extends BeaconException {

    public UserWithoutCompanyException() {
        super(new ErrorCode(BAD_REQUEST, "사용자는 소속된 회사가 없습니다."));
    }

    public UserWithoutCompanyException(String message) {
        super(new ErrorCode(BAD_REQUEST, message));
    }
}
