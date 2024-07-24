package com.mmp.beacon.commute.application;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 현재 시간을 제공하는 서비스 인터페이스입니다.
 */
public interface TimeService {

    /**
     * 현재 시간을 반환합니다.
     *
     * @return 현재 LocalDateTime 객체
     */
    LocalDateTime nowDateTime();

    /**
     * 현재 날짜를 반환합니다.
     *
     * @return 현재 LocalDate 객체
     */
    LocalDate nowDate();
}
