package com.mmp.beacon.commute.application.schedule;

import com.mmp.beacon.company.domain.Company;

/**
 * 회사별 스케줄 작업을 관리하기 위한 인터페이스입니다.
 */
public interface ScheduleService {

    /**
     * 회사별 스케줄 작업을 등록합니다.
     *
     * @param company 회사 엔티티
     */
    void scheduleCompanyTasks(Company company);
}
