package com.maple.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling // Spring Scheduling 활성화
@RequiredArgsConstructor
@Slf4j
public class JobScheduler {
    private final Job job;
    private final JobLauncher jobLauncher;

    // @Scheduled(cron = "0 0 3 * * ?") // 매일 새벽 3시
    @Scheduled(initialDelay = 1000)
    public void runJob() {
        executeJob();
    }

    public void executeJob() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    // JobParameters는 Job 인스턴스 식별을 위해 고유한 값을 포함해야 함
                    .toJobParameters();

            JobExecution jobExecution = jobLauncher.run(job, jobParameters);

            if (jobExecution.getStatus() == BatchStatus.FAILED) {
                log.error("Job Execution Failed");
            }
        } catch (Exception e) {
            log.error("Job Execution Failed");
        }
    }
}

    /*
        [Trouble Shooting]
        기존 Line 34의 경우 (jobExecution.getStatus() == BatchStatus.COMPLETED) 조건을 만족할 때 소요 시간을 로깅
        그러나 BatchConfig.java에서 각 Step을 비동기 병렬 처리하도록 변경한 후,
        Job이 성공적으로 수행되었음에도 소요 시간을 측정할 수 없는 문제가 발생
        원인은 각 Step이 비동기 병렬 처리 중이기 때문에, jobLauncher.run() 메서드를 호출한 이후 BatchStatus가 STARTING이였기 때문
        즉, 기존 로깅 방식이 잘못되었음을 알 수 있었고 JobExecutionListener를 통해 로깅하도록 변경함
    */
