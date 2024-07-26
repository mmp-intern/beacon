package com.mmp.beacon.user.query.repository;

import com.mmp.beacon.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomUserRepository {
    Page<User> findByCompanyIdAndSearchTerm(Long companyId, String searchTerm, String searchBy, Pageable pageable);
}
