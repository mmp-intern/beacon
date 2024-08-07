package com.mmp.beacon.commute.application.schedule;

import com.mmp.beacon.commute.application.CommuteService;
import com.mmp.beacon.commute.application.TimeService;
import com.mmp.beacon.company.domain.Company;
import com.mmp.beacon.company.domain.repository.CompanyRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

/**
 * 회사별 스케줄 작업을 관리하는 서비스 클래스입니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyScheduleService implements ScheduleService {

    private final TaskScheduler taskScheduler;
    private final CommuteService commuteService;
    private final TimeService timeService;
    private final CompanyRepository companyRepository;

    /**
     * 서버 시작 시 스케줄 작업을 등록합니다.
     */
    @PostConstruct
    public void init() {
        log.info("서버 시작 시 스케줄 작업을 초기화합니다.");
        scheduleDailyCompanyTasks();
    }

    /**
     * 매일 모든 회사의 출근 및 퇴근 시간을 기반으로 스케줄 작업을 등록합니다.
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void scheduleDailyCompanyTasks() {
        log.info("매일 회사별 스케줄 작업을 등록 시작");
        companyRepository.findAll().forEach(this::scheduleCompanyTasks);
        log.info("매일 회사별 스케줄 작업을 등록 완료");
    }

    /**
     * 회사의 출근 및 퇴근 시간을 기반으로 스케줄 작업을 등록합니다.
     *
     * @param company 회사 엔티티
     */
    @Override
    public void scheduleCompanyTasks(Company company) {
        log.info("회사({})의 스케줄 작업을 등록 시작", company.getId());
        scheduleTask(company.getStartTime(), () -> commuteService.markLateArrivals(company.getId()));
        scheduleTask(company.getEndTime(), () -> commuteService.markAbsentees(company.getId()));
        log.info("회사({})의 스케줄 작업을 등록 완료", company.getId());
    }

    /**
     * 기존 스케줄 작업을 취소하고 새로운 스케줄 작업을 등록합니다.
     *
     * @param company 회사 엔티티
     */
    @Override
    public void rescheduleCompanyTasks(Company company) {
        log.info("회사({})의 스케줄 작업을 재등록 시작", company.getId());
        scheduleCompanyTasks(company);
        log.info("회사({})의 스케줄 작업 재등록 완료", company.getId());
    }

    /**
     * 주어진 시간에 스케줄 작업을 등록합니다.
     *
     * @param time 작업 실행 시간
     * @param task 실행할 작업
     */
    private void scheduleTask(LocalTime time, Runnable task) {
        Trigger trigger = createTrigger(timeService.nowDateTime().with(time));
        taskScheduler.schedule(task, trigger);
    }

    /**
     * 주어진 시간에 실행되는 트리거를 생성합니다.
     *
     * @param time 작업 실행 시간
     * @return 생성된 트리거
     */
    private Trigger createTrigger(LocalDateTime time) {
        return triggerContext -> time.atZone(ZoneId.systemDefault()).toInstant();
    }
}
