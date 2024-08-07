package com.mmp.beacon.user.domain.repository;

import com.mmp.beacon.user.domain.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    List<Admin> findByCompanyId(Long companyId);
}
