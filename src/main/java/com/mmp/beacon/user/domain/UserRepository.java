package com.mmp.beacon.user.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<AbstractUser, Long> {
    AbstractUser findByUserId(String userId);
}
