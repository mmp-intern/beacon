package com.mmp.beacon.user.domain.repository;

import com.mmp.beacon.user.domain.AbstractUser;
import com.mmp.beacon.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 소프트 삭제되지 않은 사용자를 ID로 조회합니다.
     *
     * @param id 사용자 ID
     * @return 소프트 삭제되지 않은 사용자
     */
    Optional<User> findByIdAndIsDeletedFalse(Long id);

    /**
     * 소프트 삭제되지 않은 모든 사용자를 조회합니다.
     *
     * @return 소프트 삭제되지 않은 사용자 목록
     */
    List<User> findAllByIsDeletedFalse();

    /**
     * 소프트 삭제되지 않은 사용자를 회사 ID로 조회합니다.
     *
     * @param companyId 회사 ID
     * @return 소프트 삭제되지 않은 사용자 목록
     */
    List<User> findByCompanyIdAndIsDeletedFalse(Long companyId);

    /**
     * 삭제되지 않은 사용자들을 페이지네이션으로 가져오기 위한 메서드
     * @param pageable 페이지네이션 정보
     * @return 페이징된 사용자 목록
     */
    Page<User> findAllByIsDeletedFalse(Pageable pageable);

    Page<User> findByUserIdContainingAndIsDeletedFalse(String userId, Pageable pageable);

    Page<User> findByNameContainingAndIsDeletedFalse(String name, Pageable pageable);
}
