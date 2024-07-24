package com.mmp.beacon.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * SchedulerConfig 클래스는 스케줄링을 위한 TaskScheduler를 설정합니다.
 */
@Configuration
public class SchedulerConfig {

    /**
     * TaskScheduler 빈을 생성합니다.
     * 스레드 풀의 크기와 스레드 이름 접두사를 설정합니다.
     *
     * @return TaskScheduler 인스턴스
     */
    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(10);
        taskScheduler.setThreadNamePrefix("scheduler-");
        taskScheduler.initialize();
        return taskScheduler;
    }
}
