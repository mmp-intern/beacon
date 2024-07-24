package com.mmp.beacon.beacon.domain.repository;

import com.mmp.beacon.beacon.domain.Beacon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BeaconRepository extends JpaRepository<Beacon, Long> {

    /**
     * 비콘 MAC 주소로 비콘을 찾습니다.
     *
     * @param macAddr 비콘 MAC 주소
     * @return 해당 MAC 주소에 해당하는 비콘
     */
    Optional<Beacon> findByMacAddr(String macAddr);
}
