package com.mmp.beacon.user.domain.repository;

import com.mmp.beacon.user.domain.SuperAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SuperAdminRepository extends JpaRepository<SuperAdmin, Long> {
    List<SuperAdmin> findByCompanyId(Long companyId);
}
