package com.mmp.beacon.commute.domain.repository;

import com.mmp.beacon.commute.domain.Commute;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface CustomCommuteRepository {

    /**
     * 회사 ID와 기간, 검색 조건에 따라 출퇴근 기록을 조회합니다.
     *
     * @param companyId 회사 ID
     * @param startDate 조회 시작 날짜
     * @param endDate 조회 종료 날짜
     * @param searchTerm 검색어 (사용자 ID 또는 이름)
     * @param searchBy 검색 기준 ("id" 또는 "name")
     * @param pageable 페이지네이션 정보
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
}
