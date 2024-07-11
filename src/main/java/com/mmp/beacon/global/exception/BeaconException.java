package com.mmp.beacon.global.exception;

import lombok.Getter;

import static com.mmp.beacon.global.exception.ErrorCode.INTERNAL_SERVER_ERROR_CODE;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Getter
public class BeaconException extends RuntimeException {

    private final ErrorCode errorCode;

    public BeaconException(ErrorCode errorCode) {
        super(errorCode.message());
        this.errorCode = errorCode;
    }

    public BeaconException(String message) {
        super(message);
        this.errorCode = new ErrorCode(INTERNAL_SERVER_ERROR, message);
    }

    public BeaconException() {
        this.errorCode = INTERNAL_SERVER_ERROR_CODE;
    }
}
