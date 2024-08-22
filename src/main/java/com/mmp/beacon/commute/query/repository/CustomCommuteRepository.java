package com.mmp.beacon.commute.query.repository;

import com.mmp.beacon.commute.domain.Commute;
import com.mmp.beacon.commute.query.response.CommuteRecordInfo;
import com.mmp.beacon.commute.query.response.CommuteStatisticsResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface CustomCommuteRepository {

    /**
     * 회사 ID와 기간, 검색 조건에 따라 출퇴근 기록을 조회합니다.
     * 해당 기간 동안 회사에 소속된 사용자의 출퇴근 기록을 검색합니다.
     *
     * @param companyId  회사 ID
     * @param startDate  조회 시작 날짜
     * @param endDate    조회 종료 날짜
     * @param searchTerm 검색어 (사용자 ID 또는 이름)
     * @param searchBy   검색 기준 ("id" 또는 "name")
     * @param pageable   페이지네이션 정보
     * @return 조건에 따른 출퇴근 기록 페이지
     */
    Page<Commute> findByCompanyIdAndPeriodAndSearchTerm(
            Long companyId,
            LocalDate startDate,
            LocalDate endDate,
            String searchTerm,
            String searchBy,
            Pageable pageable
    );

    /**
     * 회사 ID, 날짜, 검색 조건에 따라 사용자와 출퇴근 기록을 조회합니다.
     * 해당 날짜에 회사에 소속된 사용자의 출퇴근 기록을 검색합니다.
     *
     * @param companyId  회사 ID
     * @param date       조회할 날짜
     * @param searchTerm 검색어 (사용자 ID 또는 이름)
     * @param searchBy   검색 기준 ("id" 또는 "name")
     * @param pageable   페이지네이션 정보
     * @return 조건에 따른 사용자와 출퇴근 기록 페이지
     */
    Page<CommuteRecordInfo> findByCompanyIdAndDateAndSearchTerm(
            Long companyId,
            LocalDate date,
            String searchTerm,
            String searchBy,
            Pageable pageable
    );

    /**
     * 회사 ID, 기간, 검색 조건에 따라 출퇴근 통계 정보를 조회합니다.
     * 회사에 소속된 사용자의 출퇴근 통계 데이터를 특정 기간 동안 검색합니다.
     *
     * @param companyId  조회할 회사의 ID
     * @param startDate  통계 조회 시작 날짜
     * @param endDate    통계 조회 종료 날짜
     * @param searchTerm 검색어 (사용자 ID 또는 이름)
     * @param searchBy   검색 기준 (사용자 ID는 "id", 사용자 이름은 "name"으로 검색)
     * @param pageable   페이지네이션 정보
     * @return 조건에 따른 출퇴근 통계 정보 페이지
     */
    Page<CommuteStatisticsResponse> findCommuteStatistics(
            Long companyId,
            LocalDate startDate,
            LocalDate endDate,
            String searchTerm,
            String searchBy,
            Pageable pageable
    );
}