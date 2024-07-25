package com.mmp.beacon.user.domain.repository;

import com.mmp.beacon.user.domain.AbstractUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AbstractUserRepository extends JpaRepository<AbstractUser, Long> {
}
