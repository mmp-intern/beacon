package com.mmp.beacon.user.domain.repository;

import com.mmp.beacon.user.domain.User;
import com.mmp.beacon.user.query.repository.CustomUserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, CustomUserRepository {

    /**
     * 주어진 회사 ID를 사용하여 해당 회사에 소속된 모든 사용자를 조회합니다.
     *
     * @param companyId 조회할 회사의 ID
     * @return 해당 회사에 소속된 사용자 목록
     */
    List<User> findByCompanyId(Long companyId);

    /**
     * 주어진 회사 ID를 사용하여 해당 회사에 소속된 모든 사용자를 페이지네이션하여 조회합니다.
     *
     * @param companyId 조회할 회사의 ID
     * @param pageable  페이지 정보
     * @return 페이징된 사용자 목록
     */
    Page<User> findByCompanyId(Long companyId, Pageable pageable);


    Optional<User> findByUserId(String userId);


}
