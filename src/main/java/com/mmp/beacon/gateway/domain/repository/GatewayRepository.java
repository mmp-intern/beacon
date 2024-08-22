package com.mmp.beacon.gateway.domain.repository;

import com.mmp.beacon.gateway.domain.Gateway;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GatewayRepository extends JpaRepository<Gateway, Long> {

    /**
     * 소프트 삭제되지 않은 게이트웨이를 MAC 주소로 찾습니다.
     *
     * @param macAddr 게이트웨이 MAC 주소
     * @return 소프트 삭제되지 않은 해당 MAC 주소에 해당하는 게이트웨이
     */
    Optional<Gateway> findByMacAddrAndIsDeletedFalse(String macAddr);

    /**
     * 소프트 삭제되지 않은 게이트웨이를 ID로 찾습니다.
     *
     * @param id 게이트웨이 ID
     * @return 소프트 삭제되지 않은 해당 ID에 해당하는 게이트웨이
     */
    Optional<Gateway> findByIdAndIsDeletedFalse(Long id);
}
