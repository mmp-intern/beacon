package com.mmp.beacon.user.domain.repository;

import com.mmp.beacon.user.domain.SuperAdmin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface SuperAdminRepository extends JpaRepository<SuperAdmin, Long> {

    /**
     * 소프트 삭제되지 않은 슈퍼 관리자를 ID로 조회합니다.
     *
     * @param id 사용자 ID
     * @return 소프트 삭제되지 않은 슈퍼 관리자
     */
    Optional<SuperAdmin> findByIdAndIsDeletedFalse(Long id);
}
