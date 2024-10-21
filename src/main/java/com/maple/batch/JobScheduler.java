package com.maple.batch;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

/**
 * 정해진 시간에 배치 작업을 실행하는 Scheduler 클래스
 */
@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class JobScheduler {
    private final Job myJob;
    private final JobLauncher jobLauncher;

    /**
     * 매일 오전 03:00에 배치 작업을 실행하는 메서드
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void runJob() {
        executeJob();
    }

    /**
     * 스프링 빈 초기화 시 배치 작업을 실행하는 메서드
     * 스프링 빈의 이벤트 라이프사이클: 스프링 컨테이너 생성 → 스프링 빈 생성 → 의존관계 주입 → 초기화 콜백 → 사용 → 소멸 전 콜백 → 스프링 종료
     * PostConstruct : 초기화 콜백 (빈이 생성되고, 빈의 의존관계 주입이 완료된 후 호출)
     * PreDestroy : 소멸 전 콜백 (빈이 소멸되기 직전에 호출)
     */
    @PostConstruct
    public void init() {
        executeJob();
    }

    /**
     * 배치 작업을 실행하는 메서드
     */
    public void executeJob() {
        try {
            // JobParameters는 Job 인스턴스 식별을 위해 고유한 값을 포함해야 함
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(myJob, jobParameters);
        } catch (Exception e) {
            log.error("Job Execution Error: ", e);
        }
    }
}
