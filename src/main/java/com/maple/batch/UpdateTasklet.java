package com.maple.batch;

import com.maple.service.UpdateService;
import org.springframework.stereotype.Component;

/**
 * 클라이언트 업데이트 관련 작업을 수행하는 Tasklet 클래스
 */
@Component
public class UpdateTasklet extends BaseTasklet<UpdateService> {
    /**
     * 생성자 메서드
     * @param updateService UpdateService 객체
     */
    public UpdateTasklet(UpdateService updateService) {
        super(updateService);
    }

    /**
     * 클라이언트 업데이트 정보 갱신 메서드
     */
    @Override
    protected void fetchData() {
        informationService.fetchUpdates();
    }
}
