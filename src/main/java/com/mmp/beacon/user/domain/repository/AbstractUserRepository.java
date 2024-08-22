package com.mmp.beacon.user.domain.repository;

import com.mmp.beacon.user.domain.AbstractUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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


    /**
     * 삭제되지 않은 사용자들을 페이지네이션으로 가져오기 위한 메서드
     * @param pageable 페이지네이션 정보
     * @return 페이징된 사용자 목록
     */
    Page<AbstractUser> findAllByIsDeletedFalse(Pageable pageable);
}
