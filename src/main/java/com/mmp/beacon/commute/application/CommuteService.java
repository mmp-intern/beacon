package com.mmp.beacon.commute.application;

import com.mmp.beacon.beacon.domain.Beacon;
import com.mmp.beacon.beacon.domain.repository.BeaconRepository;
import com.mmp.beacon.commute.application.command.BeaconData;
import com.mmp.beacon.commute.domain.AttendanceStatus;
import com.mmp.beacon.commute.domain.Commute;
import com.mmp.beacon.commute.domain.WorkStatus;
import com.mmp.beacon.commute.domain.repository.CommuteRepository;
import com.mmp.beacon.gateway.domain.Gateway;
import com.mmp.beacon.gateway.domain.repository.GatewayRepository;
import com.mmp.beacon.user.domain.User;
import com.mmp.beacon.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommuteService {

    private final GatewayRepository gatewayRepository;
    private final UserRepository userRepository;
    private final CommuteRepository commuteRepository;
    private final BeaconRepository beaconRepository;
    private final TimeService timeService;

    /**
     * 주어진 게이트웨이 MAC 주소와 비콘 데이터 리스트를 사용하여 근태 관리를 수행합니다.
     * 오늘이 근무일이 아닌 경우 로그를 남기고 처리를 중단합니다.
     * 게이트웨이를 찾은 후, 각 비콘 데이터를 개별적으로 처리합니다.
     * 만약 게이트웨이를 찾지 못하면 경고 로그를 남깁니다.
     *
     * @param gatewayMac     게이트웨이 MAC 주소
     * @param beaconDataList 비콘 데이터 리스트
     */
    @Transactional
    public void processAttendance(String gatewayMac, List<BeaconData> beaconDataList) {
        if (isWeekend()) {
            log.info("오늘은 근무일이 아니어서 비콘 데이터 처리를 중단합니다.");
            return;
        }

        gatewayRepository.findByMacAddrAndIsDeletedFalse(gatewayMac)
                .ifPresentOrElse(
                        gateway -> beaconDataList.forEach(beaconData -> handleBeaconData(gateway, beaconData)),
                        () -> log.warn("게이트웨이 {}가 존재하지 않아 비콘 데이터 처리를 중단합니다.", gatewayMac)
                );
    }

    /**
     * 주어진 게이트웨이와 비콘 데이터를 사용하여 개별 비콘 데이터를 처리합니다.
     * 사용자를 찾고, 사용자가 속한 회사와 게이트웨이가 속한 회사가 일치하는 경우에만 데이터를 처리합니다.
     * 일치하지 않으면 경고 로그를 남깁니다.
     *
     * @param gateway    게이트웨이 엔티티
     * @param beaconData 비콘 데이터
     */
    private void handleBeaconData(Gateway gateway, BeaconData beaconData) {
        beaconRepository.findByMacAddrAndIsDeletedFalse(beaconData.mac())
                .map(Beacon::getUser)
                .ifPresentOrElse(
                        user -> {
                            if (user.getCompany().equals(gateway.getCompany())) {
                                processBeaconData(user, beaconData);
                            } else {
                                log.warn("게이트웨이 {}와 사용자 {}의 회사가 일치하지 않아 비콘 데이터 처리를 중단합니다.",
                                        gateway.getMacAddr(), user.getId());
                            }
                        },
                        () -> log.warn("비콘 MAC 주소 {}에 해당하는 사용자가 존재하지 않아 비콘 데이터 처리를 중단합니다.", beaconData.mac())
                );
    }

    /**
     * 주어진 사용자와 비콘 데이터를 사용하여 비콘 데이터를 처리합니다.
     * 사용자의 출퇴근 기록을 찾고, 존재하는 경우 업데이트하고, 존재하지 않는 경우 새로 생성합니다.
     *
     * @param user       사용자 엔티티
     * @param beaconData 비콘 데이터
     */
    private void processBeaconData(User user, BeaconData beaconData) {
        LocalDate today = timeService.nowDate();
        commuteRepository.findByUserAndDateAndIsDeletedFalse(user, today)
                .ifPresentOrElse(
                        commute -> handleExistingCommute(commute, beaconData),
                        () -> handleNewCommute(user, beaconData)
                );
    }

    /**
     * 기존 출퇴근 기록이 있는 경우 이를 갱신합니다.
     * 출퇴근 기록의 근무 상태와 시간을 업데이트한 후 저장합니다.
     *
     * @param commute    출퇴근 엔티티
     * @param beaconData 비콘 데이터
     */
    private void handleExistingCommute(Commute commute, BeaconData beaconData) {
        log.info("기존 출퇴근 기록을 갱신합니다. Commute ID: {}", commute.getId());
        commute.updateWorkStatus(WorkStatus.IN_OFFICE);
        commute.updateTimestamps(beaconData.earlyTimestamp().toLocalTime(), beaconData.lateTimestamp().toLocalTime());
        commuteRepository.save(commute);
    }

    /**
     * 새로운 출퇴근 기록을 생성합니다.
     * 만약 비콘 데이터의 earlyTimestamp가 설정된 출근 시간보다 이르면 출근 처리하고,
     * 그렇지 않으면 지각으로 기록합니다.
     *
     * @param user       사용자 엔티티
     * @param beaconData 비콘 데이터
     */
    private void handleNewCommute(User user, BeaconData beaconData) {
        if (beaconData.earlyTimestamp().toLocalTime().isBefore(user.getCompany().getStartTime())) {
            markPresent(user, beaconData);
        } else {
            markLateArrival(user);
        }
    }

    /**
     * 사용자의 출근 기록을 생성합니다.
     * 주어진 사용자, 비콘 데이터, 출석 상태, 근무 상태를 사용하여 출퇴근 기록을 생성하고 저장합니다.
     *
     * @param user       사용자 엔티티
     * @param beaconData 비콘 데이터
     */
    private void markPresent(User user, BeaconData beaconData) {
        log.info("사용자의 출근 기록을 생성합니다. 사용자 ID: {}", user.getId());
        commuteRepository.save(Commute.builder()
                .user(user)
                .date(beaconData.earlyTimestamp().toLocalDate())
                .startedAt(beaconData.earlyTimestamp().toLocalTime())
                .endedAt(beaconData.lateTimestamp().toLocalTime())
                .attendanceStatus(AttendanceStatus.PRESENT)
                .workStatus(WorkStatus.IN_OFFICE)
                .build());
    }

    /**
     * 주어진 회사 ID를 사용하여 지각자를 기록합니다.
     * 오늘이 근무일이 아닌 경우, 로그를 남기고 처리를 중단합니다.
     * 주어진 회사의 모든 사용자를 조회하여 출퇴근 기록이 없는 사용자를 지각자로 기록합니다.
     * 이 메서드는 스케줄러에 의해 사용자 회사의 출근 시간에 호출됩니다.
     *
     * @param companyId 기록할 회사의 ID
     */
    @Transactional
    public void markLateArrivals(Long companyId) {
        if (isWeekend()) {
            log.info("오늘은 근무일이 아니어서 지각 처리를 중단합니다.");
            return;
        }
        log.info("회사 ID {}에 대한 지각자를 기록합니다.", companyId);
        LocalDate today = timeService.nowDate();
        userRepository.findByCompanyId(companyId).stream()
                .filter(user -> commuteRepository.findByUserAndDateAndIsDeletedFalse(user, today).isEmpty())
                .forEach(this::markLateArrival);
    }

    /**
     * 주어진 사용자를 지각자로 기록합니다.
     * 사용자와 현재 날짜를 사용하여 새로운 출퇴근 기록을 생성하고, 지각 상태로 저장합니다.
     *
     * @param user 사용자 엔티티
     */
    private void markLateArrival(User user) {
        log.info("사용자를 지각자로 기록합니다. 사용자 ID: {}", user.getId());
        commuteRepository.save(Commute.builder()
                .user(user)
                .date(timeService.nowDate())
                .startedAt(null)
                .endedAt(null)
                .attendanceStatus(AttendanceStatus.LATE)
                .workStatus(WorkStatus.OUT_OFF_OFFICE)
                .build());
    }

    /**
     * 오늘의 퇴근/자리 비움을 기록합니다.
     * 오늘이 근무일이 아닌 경우 로그를 남기고 처리를 중단합니다.
     * 모든 사용자를 조회하여 출퇴근 기록이 존재하고, 근무 중이면서 일정 시간동안 비콘이 감지되지 않은 경우를 기록합니다.
     * 이 메소드는 스케줄러에 의해 주기적으로 호출됩니다.
     */
    @Transactional
    public void markLeaveOrOutOffice() {
        if (isWeekend()) {
            log.info("오늘은 근무일이 아니어서 퇴근 처리를 중단합니다.");
            return;
        }
        LocalDate today = timeService.nowDate();
        LocalDateTime nowMinus5Minutes = timeService.nowDateTime().minusMinutes(5);
        userRepository.findAll().forEach(user -> commuteRepository.findByUserAndDateAndIsDeletedFalse(user, today)
                .ifPresent(commute -> {
                    if (commute.getWorkStatus() == WorkStatus.IN_OFFICE &&
                            nowMinus5Minutes.isAfter(commute.getEndedAt().atDate(today))) {
                        log.info("사용자 ID {}가 자리를 비웠습니다.", user.getId());
                        commute.updateWorkStatus(WorkStatus.OUT_OFF_OFFICE);
                        commuteRepository.save(commute);
                    }
                }));
    }

    /**
     * 주어진 회사의 ID로 결근자를 기록합니다.
     * 오늘이 근무일이 아닌 경우 로그를 남기고 처리를 중단합니다.
     * 주어진 회사의 모든 사용자를 조회하여 출퇴근 기록이 존재하고, 지각 상태이며 출근 및 퇴근 시간이 없는 사용자를 결근자로 기록합니다.
     * 이 메소드는 스케줄러에 의해 사용자 회사의 퇴근 시간에 호출됩니다.
     */
    @Transactional
    public void markAbsentees(Long companyId) {
        if (isWeekend()) {
            log.info("오늘은 근무일이 아니어서 결근 처리를 중단합니다.");
            return;
        }
        log.info("회사 ID {}에 대한 결근자를 기록합니다.", companyId);
        LocalDate today = timeService.nowDate();
        userRepository.findByCompanyId(companyId).stream()
                .map(user -> commuteRepository.findByUserAndDateAndIsDeletedFalse(user, today))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(commute -> commute.getAttendanceStatus() == AttendanceStatus.LATE && commute.getStartedAt() == null && commute.getEndedAt() == null)
                .forEach(this::markAbsent);
    }

    /**
     * 주어진 출퇴근 기록을 결근으로 기록합니다.
     * 출퇴근 기록의 출석 상태를 결근으로 업데이트한 후 저장합니다.
     *
     * @param commute 출퇴근 엔티티
     */
    private void markAbsent(Commute commute) {
        log.info("출퇴근 기록을 결근으로 업데이트합니다. Commute ID: {}", commute.getId());
        commute.updateAttendanceStatus(AttendanceStatus.ABSENT);
        commuteRepository.save(commute);
    }

    /**
     * 오늘이 근무일인지 확인합니다.
     * 오늘 날짜의 요일을 확인하여 토요일이나 일요일이 아닌 경우 근무일로 간주합니다.
     *
     * @return 오늘이 근무일이면 true, 아니면 false
     */
    private boolean isWeekend() {
        LocalDate today = timeService.nowDate();
        DayOfWeek dayOfWeek = today.getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }
}
