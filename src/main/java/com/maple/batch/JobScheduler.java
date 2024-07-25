package com.maple.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class JobScheduler {
    private final JobLauncher jobLauncher;
    private final Job job;

    @Scheduled(fixedRate = 60000)
    public void runNoticeJob() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                // JobParameters는 Job 인스턴스 식별을 위해 고유한 값을 포함해야 함
                .toJobParameters();

        JobExecution jobExecution = jobLauncher.run(job, jobParameters);
        log.info("Job Execution : " + jobExecution);
    }
}
