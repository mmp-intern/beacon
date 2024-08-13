package com.mmp.beacon.company.exception;

import com.mmp.beacon.global.exception.BeaconException;
import com.mmp.beacon.global.exception.ErrorCode;

import static org.springframework.http.HttpStatus.NOT_FOUND;

public class CompanyNotFoundException extends BeaconException {

    public CompanyNotFoundException() {
        super(new ErrorCode(NOT_FOUND, "회사를 찾을 수 없습니다."));
    }

    public CompanyNotFoundException(String message) {
        super(new ErrorCode(NOT_FOUND, message));
    }
}
