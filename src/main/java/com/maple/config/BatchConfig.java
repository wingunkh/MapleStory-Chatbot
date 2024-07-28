package com.maple.config;

import com.maple.batch.NoticeTasklet;
import com.maple.batch.UpdateTasklet;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.transaction.PlatformTransactionManager;

/*
    Job: 하나의 배치 처리를 실행하는 전체 작업 단위
    ▶ 구성요소: 이름 + Step + 재실행 여부

    Step: Job을 구성하는 개별 작업 단위
    ▶ 구성요소: Tasklet 또는 Reader/Processor/Writer

    Tasklet: 단일 작업 단위

    JobRepository: 배치 처리 중 메타데이터 테이블에 액세스하는 클래스

    SimpleAsyncTaskExecutor: 각 작업마다 새로운 스레드를 생성하여 비동기 작업을 수행하는 Executor의 구현체

    Flow: 여러 Step을 순차적 또는 병렬적으로 실행하는 제어 흐름 단위

    JobExecutionListener: Job의 시작과 종료 시점에 추가 작업을 수행할 수 있도록 콜백 메서드를 제공
 */
@Configuration // Spring 설정 클래스 정의
@EnableBatchProcessing // Spring Batch 활성화
@EnableRetry // Spring Retry 활성화
@RequiredArgsConstructor
@Slf4j
public class BatchConfig {
    @Bean
    public Job myJob(JobRepository jobRepository, Flow parallelFlow, JobExecutionListener jobExecutionListener) {
        return new JobBuilder("myJob", jobRepository)
                .listener(jobExecutionListener)
                .start(parallelFlow)
                .end()
                .build();
    }

//    @Bean
//    public Job myJob(JobRepository jobRepository, Step noticeStep, Step updateStep, JobExecutionListener jobExecutionListener) {
//        return new JobBuilder("myJob", jobRepository)
//                .listener(jobExecutionListener)
//                .start(noticeStep)
//                .next(updateStep)
//                .build();
//    }

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

    @Bean
    public TaskExecutor taskExecutor() {
        return new SimpleAsyncTaskExecutor("batchExecutor");
    }

    @Bean
    public Flow parallelFlow(Step noticeStep, Step updateStep) {
        return new FlowBuilder<Flow>("parallelFlow")
                .split(taskExecutor())
                .add(
                        new FlowBuilder<Flow>("flow1").start(noticeStep).build(),
                        new FlowBuilder<Flow>("flow2").start(updateStep).build()
                )
                .build();
    }

    @Bean
    public JobExecutionListener jobExecutionListener() {
        return new JobExecutionListener() {
            private long startTime;

            @Override
            public void beforeJob(@Nonnull JobExecution jobExecution) {
                startTime = System.currentTimeMillis();
            }

            @Override
            public void afterJob(@Nonnull JobExecution jobExecution) {
                if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
                    log.info("Job Duration: {}ms", System.currentTimeMillis() - startTime);
                }
            }
        };
    }
}
