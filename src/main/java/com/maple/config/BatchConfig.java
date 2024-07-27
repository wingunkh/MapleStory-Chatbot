package com.maple.config;

import com.maple.batch.NoticeTasklet;
import com.maple.batch.UpdateTasklet;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.transaction.PlatformTransactionManager;

/*
    Job: 하나의 배치 처리를 실행하는 전체 작업 단위
    ▶ 구성요소: 이름 + Step + 재실행 여부

    Step: Job을 구성하는 개별 작업 단위
    ▶ 구성요소: Tasklet 또는 Reader/Processor/Writer

    Tasklet: 단일 작업 단위

    JobRepository: 배치 처리 중 메타데이터 테이블에 액세스하는 클래스
 */
@Configuration
@EnableRetry
@RequiredArgsConstructor
public class BatchConfig extends DefaultBatchConfiguration {
    @Bean
    public Job myJob(JobRepository jobRepository, Step noticeStep, Step updateStep) {
        return new JobBuilder("myJob", jobRepository)
                .start(noticeStep)
                .next(updateStep)
                .build();
    }

    @Bean
    public Step noticeStep(JobRepository jobRepository, NoticeTasklet noticeTasklet, PlatformTransactionManager transactionManager) {
        return new StepBuilder("noticeStep", jobRepository)
                .tasklet(noticeTasklet, transactionManager)
                .build();
    }

    @Bean
    public Step updateStep(JobRepository jobRepository, UpdateTasklet updateTasklet, PlatformTransactionManager transactionManager) {
        return new StepBuilder("updateStep", jobRepository)
                .tasklet(updateTasklet, transactionManager)
                .build();
    }
}
