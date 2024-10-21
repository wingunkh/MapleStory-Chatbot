package com.maple.batch;

import com.maple.service.InformationService;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

/**
 * Tasklet 추상 클래스
 */
@RequiredArgsConstructor
public abstract class BaseTasklet<T extends InformationService> implements Tasklet {
    protected final T informationService;

    /**
     * 하위 클래스에서 구현해야 하는 정보 갱신 메서드
     */
    protected abstract void fetchData();

    /**
     * Tasklet 실행 메서드
     * @param stepContribution 현재 Step의 실행 상태를 담고 있는 객체
     * @param chunkContext 현재 Chunk의 메타데이터를 담고 있는 객체
     * @return Tasklet의 실행 결과 상태
     */
    @Override
    @Retryable(
            retryFor = Exception.class, // 모든 예외에 대해 재시도
            maxAttempts = 3, // 최대 재시도 횟수 3회 (첫 번째 시도 포함)
            backoff = @Backoff(delay = 3600000L) // 재시도 지연 시간 1시간
    )
    public RepeatStatus execute(@Nonnull StepContribution stepContribution, @Nonnull ChunkContext chunkContext) {
        fetchData();

        return RepeatStatus.FINISHED;
    }
}
