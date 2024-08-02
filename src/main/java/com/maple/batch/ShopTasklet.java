package com.maple.batch;

import com.maple.service.ShopService;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShopTasklet implements Tasklet {
    private final ShopService shopService;

    @Override
    @Retryable(retryFor = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 3600000L))
    // 모든 예외에 대해, 재시도 최대 횟수 3회 (첫번째 시도 포함), 재시도 지연 시간 1시간
    public RepeatStatus execute(@Nonnull StepContribution contribution, @Nonnull ChunkContext chunkContext) {
        shopService.fetchShops();

        return RepeatStatus.FINISHED;
    }
}
