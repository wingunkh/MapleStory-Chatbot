package com.maple.config;

import com.maple.batch.EventTasklet;
import com.maple.batch.NoticeTasklet;
import com.maple.batch.ShopTasklet;
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

/**
 * Spring Batch 설정 클래스
 * - Job: 하나의 배치 처리를 실행하는 전체 작업 단위 (구성요소: 이름 + Step + 재실행 여부)
 * - Step: Job을 구성하는 개별 작업 단위 (구성요소: Tasklet 또는 Reader/Processor/Writer)
 * - Tasklet: 단일 작업 단위
 * - Flow: 여러 Step을 순차적 또는 병렬적으로 실행하는 제어 흐름 단위
 */
@Configuration
@EnableBatchProcessing
@EnableRetry
@RequiredArgsConstructor
@Slf4j
public class BatchConfig {
    /**
     * 배치 작업을 정의하는 메서드
     * @param jobRepository Spring Batch 메타데이터 테이블 접근 객체
     * @param parallelFlow 병렬 실행할 Flow
     * @param jobExecutionListener Job의 실행 전후에 콜백 메서드를 제공하는 리스너
     * @return Job 객체
     */
    @Bean
    public Job myJob(JobRepository jobRepository, Flow parallelFlow, JobExecutionListener jobExecutionListener) {
        return new JobBuilder("myJob", jobRepository)
                .listener(jobExecutionListener)
                .start(parallelFlow)
                .end()
                .build();
    }

    /**
     * 공지사항 정보 갱신 Step 정의 메서드
     * @param jobRepository Spring Batch 메타데이터 테이블 접근 객체
     * @param noticeTasklet 공지사항 정보 갱신 Tasklet
     * @param transactionManager 트랜잭션 매니저
     * @return Step 객체
     */
    @Bean
    public Step noticeStep(JobRepository jobRepository, NoticeTasklet noticeTasklet, PlatformTransactionManager transactionManager) {
        return new StepBuilder("noticeStep", jobRepository)
                .tasklet(noticeTasklet, transactionManager)
                .build();
    }

    /**
     * 클라이언트 업데이트 정보 갱신 Step 정의 메서드
     * @param jobRepository Spring Batch 메타데이터 테이블 접근 객체
     * @param updateTasklet 클라이언트 업데이트 정보 갱신 Tasklet
     * @param transactionManager 트랜잭션 매니저
     * @return Step 객체
     */
    @Bean
    public Step updateStep(JobRepository jobRepository, UpdateTasklet updateTasklet, PlatformTransactionManager transactionManager) {
        return new StepBuilder("updateStep", jobRepository)
                .tasklet(updateTasklet, transactionManager)
                .build();
    }

    /**
     * 진행 중인 이벤트 정보 갱신 Step 정의 메서드
     * @param jobRepository Spring Batch 메타데이터 테이블 접근 객체
     * @param eventTasklet 진행 중인 이벤트 정보 갱신 Tasklet
     * @param transactionManager 트랜잭션 매니저
     * @return Step 객체
     */
    @Bean
    public Step eventStep(JobRepository jobRepository, EventTasklet eventTasklet, PlatformTransactionManager transactionManager) {
        return new StepBuilder("eventStep", jobRepository)
                .tasklet(eventTasklet, transactionManager)
                .build();
    }

    /**
     * 진행 중인 이벤트 정보 갱신 Step 정의 메서드
     * @param jobRepository Spring Batch 메타데이터 테이블 접근 객체
     * @param shopTasklet 진행 중인 이벤트 정보 갱신 Tasklet
     * @param transactionManager 트랜잭션 매니저
     * @return Step 객체
     */
    @Bean
    public Step shopStep(JobRepository jobRepository, ShopTasklet shopTasklet, PlatformTransactionManager transactionManager) {
        return new StepBuilder("shopStep", jobRepository)
                .tasklet(shopTasklet, transactionManager)
                .build();
    }

    /**
     * 비동기 작업을 처리하는 TaskExecutor를 정의하는 메서드
     * - 각 작업은 별도의 스레드에서 처리됨
     * @return SimpleAsyncTaskExecutor 객체
     */
    @Bean
    public TaskExecutor taskExecutor() {
        return new SimpleAsyncTaskExecutor("taskExecutor");
    }

    /**
     * 각 Step을 병렬 실행하는 Flow를 정의하는 메서드
     * @param noticeStep 공지사항 정보 갱신 Step
     * @param updateStep 클라이언트 업데이트 정보 갱신 Step
     * @param eventStep 진행 중인 이벤트 정보 갱신 Step
     * @param shopStep 캐시샵 공지사항 정보 갱신 Step
     * @return Flow 객체
     */
    @Bean
    public Flow parallelFlow(Step noticeStep, Step updateStep, Step eventStep, Step shopStep) {
        return new FlowBuilder<Flow>("parallelFlow")
                .split(taskExecutor())
                .add(
                        new FlowBuilder<Flow>("flow1").start(noticeStep).build(),
                        new FlowBuilder<Flow>("flow2").start(updateStep).build(),
                        new FlowBuilder<Flow>("flow3").start(eventStep).build(),
                        new FlowBuilder<Flow>("flow4").start(shopStep).build()
                )
                .build();
    }

    /**
     * Job의 실행 전후에 콜백 메서드를 제공하는 리스너를 정의하는 메서드
     * - Job의 실행 시간 측정 기능을 포함
     * @return JobExecutionListener 객체
     */
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
                    log.info("Job Execution Duration: {}ms", System.currentTimeMillis() - startTime);
                }
            }
        };
    }
}
