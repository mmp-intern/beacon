package com.mmp.beacon.user.domain.repository;

import com.mmp.beacon.user.domain.AbstractUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AbstractUserRepository extends JpaRepository<AbstractUser, Long> {
    Optional<AbstractUser> findByUserId(String userId);

    Optional<AbstractUser> findByUserIdAndIsDeletedFalse(String userId);
}
