package com.mmp.beacon.user.domain.repository;

import com.mmp.beacon.user.domain.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    /**
     * 소프트 삭제되지 않은 관리자를 회사 ID로 조회합니다.
     *
     * @param companyId 회사 ID
     * @return 소프트 삭제되지 않은 관리자 목록
     */
    List<Admin> findByCompanyIdAndIsDeletedFalse(Long companyId);
}
