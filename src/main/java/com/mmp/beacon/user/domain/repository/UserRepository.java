package com.mmp.beacon.user.domain.repository;

import com.mmp.beacon.user.domain.User;
import com.mmp.beacon.user.query.repository.CustomUserRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, CustomUserRepository {

    /**
     * 소프트 삭제되지 않은 사용자를 ID로 조회합니다.
     *
     * @param id 사용자 ID
     * @return 소프트 삭제되지 않은 사용자
     */
    Optional<User> findByIdAndIsDeletedFalse(Long id);

    /**
     * 소프트 삭제되지 않은 사용자를 회사 ID로 조회합니다.
     *
     * @param companyId 회사 ID
     * @return 소프트 삭제되지 않은 사용자 목록
     */
    List<User> findByCompanyIdAndIsDeletedFalse(Long companyId);
}
