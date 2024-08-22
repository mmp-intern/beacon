package com.mmp.beacon.commute.domain.repository;

import com.mmp.beacon.commute.domain.Commute;
import com.mmp.beacon.commute.query.repository.CustomCommuteRepository;
import com.mmp.beacon.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface CommuteRepository extends JpaRepository<Commute, Long>, CustomCommuteRepository {

    /**
     * 소프트 삭제되지 않은 출퇴근 기록을 ID로 찾습니다.
     *
     * @param id 출퇴근 기록 ID
     * @return 소프트 삭제되지 않은 해당 ID에 해당하는 출퇴근 기록
     */
    Optional<Commute> findByIdAndIsDeletedFalse(Long id);

    /**
     * 소프트 삭제되지 않은 사용자와 날짜로 출퇴근 기록을 찾습니다.
     *
     * @param user 사용자 엔티티
     * @param date 날짜
     * @return 해당 사용자와 날짜에 해당하는 소프트 삭제되지 않은 출퇴근 기록
     */
    Optional<Commute> findByUserAndDateAndIsDeletedFalse(User user, LocalDate date);
}
