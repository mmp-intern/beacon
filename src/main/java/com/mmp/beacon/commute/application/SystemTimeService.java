package com.mmp.beacon.commute.application;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 실제 시스템 시간을 제공하는 구현 클래스입니다.
 */
@Service
public class SystemTimeService implements TimeService {

    /**
     * 현재 시스템 날짜 및 시간을 반환합니다.
     *
     * @return 현재 LocalDateTime 객체
     */
    @Override
    public LocalDateTime nowDateTime() {
        return LocalDateTime.now();
    }

    /**
     * 현재 시스템 날짜를 반환합니다.
     *
     * @return 현재 LocalDate 객체
     */
    @Override
    public LocalDate nowDate() {
        return LocalDate.now();
    }

    /**
     * 현재 시스템 시간을 반환합니다.
     *
     * @return 현재 LocalTime 객체
     */
    @Override
    public LocalTime nowTime() {
        return LocalTime.now();
    }
}
