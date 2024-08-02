package com.mmp.beacon.user.domain.repository;

import com.mmp.beacon.user.domain.SuperAdmin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface SuperAdminRepository extends JpaRepository<SuperAdmin, Long> {
    Optional<SuperAdmin> findByUserId(String userId);
}
