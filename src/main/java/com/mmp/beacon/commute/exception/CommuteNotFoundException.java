package com.mmp.beacon.commute.exception;

import com.mmp.beacon.global.exception.BeaconException;
import com.mmp.beacon.global.exception.ErrorCode;

import static org.springframework.http.HttpStatus.NOT_FOUND;

public class CommuteNotFoundException extends BeaconException {

    public CommuteNotFoundException() {
        super(new ErrorCode(NOT_FOUND, "출퇴근 정보를 찾을 수 없습니다."));
    }

    public CommuteNotFoundException(String message) {
        super(new ErrorCode(NOT_FOUND, message));
    }
}
