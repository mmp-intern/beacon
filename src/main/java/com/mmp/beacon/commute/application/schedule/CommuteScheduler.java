package com.mmp.beacon.commute.application.schedule;

import com.mmp.beacon.commute.application.CommuteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 스케줄링 작업을 설정하고, 정해진 시간에 특정 작업을 실행하는 클래스입니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CommuteScheduler {

    private final CommuteService commuteService;

    /**
     * 매 5분마다 자리 비움 또는 퇴근 상태를 업데이트합니다.
     */
    @Scheduled(cron = "0 */5 * * * *")
    public void scheduleMarkLeaveOrOutOffice() {
        log.info("자리 비움/퇴근 처리 스케줄러 시작");
        try {
            commuteService.markLeaveOrOutOffice();
        } catch (Exception e) {
            log.error("자리 비움/퇴근 처리 중 오류 발생: {}", e.getMessage());
        }
        log.info("자리 비움/퇴근 처리 스케줄러 종료");
    }
}
