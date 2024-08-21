package com.mmp.beacon.beacon.domain.repository;

import com.mmp.beacon.beacon.domain.Beacon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.List;

@Repository
public interface BeaconRepository extends JpaRepository<Beacon, Long> {

    /**
     * 비콘 MAC 주소로 비콘을 찾습니다.
     *
     * @param macAddr 비콘 MAC 주소
     * @return 해당 MAC 주소에 해당하는 비콘
     */
    Optional<Beacon> findByMacAddr(String macAddr);

    /**
     * 소프트 삭제되지 않은 비콘을 ID로 찾습니다.
     *
     * @param id 비콘 ID
     * @return 소프트 삭제되지 않은 비콘
     */
    Optional<Beacon> findByIdAndIsDeletedFalse(Long id);

    Page<Beacon> findAllByIsDeletedFalse(Pageable pageable);
}
