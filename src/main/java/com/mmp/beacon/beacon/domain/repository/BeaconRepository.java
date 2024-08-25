package com.mmp.beacon.beacon.domain.repository;

import com.mmp.beacon.beacon.domain.Beacon;
import com.mmp.beacon.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BeaconRepository extends JpaRepository<Beacon, Long> {

    /**
     * 소프트 삭제되지 않은 비콘을 MAC 주소로 찾습니다.
     *
     * @param macAddr 비콘 MAC 주소
     * @return 소프트 삭제되지 않은 해당 MAC 주소에 해당하는 비콘
     */
    Optional<Beacon> findByMacAddrAndIsDeletedFalse(String macAddr);

    /**
     * 소프트 삭제되지 않은 비콘을 ID로 찾습니다.
     *
     * @param id 비콘 ID
     * @return 소프트 삭제되지 않은 비콘
     */
    Optional<Beacon> findByIdAndIsDeletedFalse(Long id);

    /**
     * 소프트 삭제되지 않은 모든 비콘을 페이지네이션으로 조회합니다.
     *
     * @param pageable 페이지 요청 정보
     * @return 소프트 삭제되지 않은 비콘의 페이지
     */
    Page<Beacon> findAllByIsDeletedFalse(Pageable pageable);

    /**
     * 소프트 삭제되지 않은 비콘을 사용자로 찾습니다.
     *
     * @param user 사용자
     * @return 소프트 삭제되지 않은 비콘
     */
    List<Beacon> findByUserAndIsDeletedFalse(User user);
}
