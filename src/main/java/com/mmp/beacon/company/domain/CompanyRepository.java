package com.mmp.beacon.company.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByName(String name); // 수정된 메소드 이름
}
