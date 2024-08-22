package com.mmp.beacon.user.domain.repository;

import com.mmp.beacon.user.domain.AbstractUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AbstractUserRepository extends JpaRepository<AbstractUser, Long> {

    /**
     * 소프트 삭제되지 않은 사용자를 ID로 조회합니다.
     *
     * @param id 사용자 ID
     * @return 소프트 삭제되지 않은 사용자
     */
    Optional<AbstractUser> findByIdAndIsDeletedFalse(Long id);

    /**
     * 소프트 삭제되지 않은 사용자를 UserId로 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 소프트 삭제되지 않은 사용자
     */
    Optional<AbstractUser> findByUserIdAndIsDeletedFalse(String userId);
}
