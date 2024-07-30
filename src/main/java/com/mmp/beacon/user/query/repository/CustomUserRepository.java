package com.mmp.beacon.user.query.repository;

import com.mmp.beacon.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomUserRepository {

    /**
     * 특정 회사에 소속된 사용자를 검색어와 기준에 따라 페이지네이션된 형태로 조회합니다.
     *
     * @param companyId 조회할 회사의 ID
     * @param searchTerm 검색어 (사용자 ID 또는 이름)
     * @param searchBy 검색 기준 ("id" 또는 "name")
     * @param pageable 페이지네이션 정보
     * @return 검색어와 기준에 따라 필터링된 사용자의 페이지네이션된 목록
     */
    Page<User> findByCompanyIdAndSearchTerm(Long companyId, String searchTerm, String searchBy, Pageable pageable);
}
