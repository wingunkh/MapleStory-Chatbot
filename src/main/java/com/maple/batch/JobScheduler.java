package com.maple.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
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

    @Async // 메인 스레드의 블로킹 방지를 위해 비동기 처리
    public void executeJob() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    // JobParameters는 Job 인스턴스 식별을 위해 고유한 값을 포함해야 함
                    .toJobParameters();

            JobExecution jobExecution = jobLauncher.run(job, jobParameters);

            if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
                log.info("Job Execution Completed: " + jobExecution);
            } else {
                log.error("Job Execution Failed");
            }
        } catch (Exception e) {
            log.error("Job Execution Failed");
        }
    }
}
