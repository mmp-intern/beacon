package com.mmp.beacon.gateway.domain.repository;

import com.mmp.beacon.gateway.domain.Gateway;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GatewayRepository extends JpaRepository<Gateway, Long> {

    /**
     * MAC 주소로 게이트웨이를 찾습니다.
     *
     * @param macAddr 게이트웨이 MAC 주소
     * @return 해당 MAC 주소에 해당하는 게이트웨이
     */
    Optional<Gateway> findByMacAddr(String macAddr);
}
