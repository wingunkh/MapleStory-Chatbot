package com.maple.batch;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.EnableScheduling;
import java.time.Instant;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class JobScheduler {
    private final Job job;
    private final JobLauncher jobLauncher;
    private final TaskScheduler taskScheduler;

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
                scheduleRetry();
            }
        } catch (Exception e) {
            log.error("Job Execution Failed");
            scheduleRetry();
        }
    }

    @Async // 메인 스레드의 블로킹 방지를 위해 비동기 처리
    public void retryJob() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            JobExecution jobExecution = jobLauncher.run(job, jobParameters);

            if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
                log.info("Job Retried Completed: " + jobExecution);
            } else {
                log.error("Job Retried Failed");
            }
        } catch (Exception e) {
            log.error("Job Retried Failed");
        }
    }

    private void scheduleRetry() {
        taskScheduler.schedule(this::retryJob, new RetryAfterTenSecondTrigger());
    }

    private static class RetryAfterTenSecondTrigger implements Trigger {
        @Override
        public Instant nextExecution(@Nonnull TriggerContext triggerContext) {
            return Instant.now().plusSeconds(10);
        }
    }
}
