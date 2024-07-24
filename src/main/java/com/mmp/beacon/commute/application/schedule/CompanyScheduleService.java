package com.mmp.beacon.commute.application.schedule;

import com.mmp.beacon.commute.application.CommuteService;
import com.mmp.beacon.commute.application.TimeService;
import com.mmp.beacon.company.domain.Company;
import com.mmp.beacon.company.domain.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
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
@Service
@RequiredArgsConstructor
public class CompanyScheduleService implements ScheduleService {

    private final TaskScheduler taskScheduler;
    private final CommuteService commuteService;
    private final TimeService timeService;
    private final CompanyRepository companyRepository;

    /**
     * 매일 모든 회사의 출근 및 퇴근 시간을 기반으로 스케줄 작업을 등록합니다.
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void scheduleDailyCompanyTasks() {
        companyRepository.findAll().forEach(this::scheduleCompanyTasks);
    }

    /**
     * 회사의 출근 및 퇴근 시간을 기반으로 스케줄 작업을 등록합니다.
     *
     * @param company 회사 엔티티
     */
    @Override
    public void scheduleCompanyTasks(Company company) {
        scheduleTask(company.getStartTime(), () -> commuteService.markLateArrivals(company.getId()));
        scheduleTask(company.getEndTime(), () -> commuteService.markAbsentees(company.getId()));
    }

    /**
     * 기존 스케줄 작업을 취소하고 새로운 스케줄 작업을 등록합니다.
     *
     * @param company 회사 엔티티
     */
    @Override
    public void rescheduleCompanyTasks(Company company) {
        scheduleCompanyTasks(company);
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
