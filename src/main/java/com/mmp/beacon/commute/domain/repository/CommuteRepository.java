package com.mmp.beacon.commute.domain.repository;

import com.mmp.beacon.commute.domain.Commute;
import com.mmp.beacon.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface CommuteRepository extends JpaRepository<Commute, Long> {

    /**
     * 사용자와 날짜로 출퇴근 기록을 찾습니다.
     *
     * @param user 사용자 엔티티
     * @param date 날짜
     * @return 해당 사용자와 날짜에 해당하는 출퇴근 기록
     */
    Optional<Commute> findByUserAndDate(User user, LocalDate date);

    /**
     * 특정 날짜와 회사 ID로 출퇴근 기록을 페이징하여 찾습니다.
     *
     * @param data 날짜
     * @param companyId 회사 ID
     * @param pageable 페이지 정보
     * @return 페이징된 출퇴근 기록
     */
    Page<Commute> findAllByDateAndUser_Company_Id(LocalDate data, Long companyId, Pageable pageable);
}
