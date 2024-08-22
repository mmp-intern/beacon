package com.mmp.beacon.company.domain.repository;

import com.mmp.beacon.company.domain.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    /**
     * ID로 isDeleted가 false인 회사를 조회합니다.
     *
     * @param id 회사 ID
     * @return isDeleted가 false인 해당 ID의 회사
     */
    Optional<Company> findByIdAndIsDeletedFalse(Long id);

    /**
     * 회사 이름으로 isDeleted가 false인 회사를 조회합니다.
     *
     * @param name 회사 이름
     * @return isDeleted가 false인 해당 이름의 회사
     */
    Optional<Company> findByNameAndIsDeletedFalse(String name);
}