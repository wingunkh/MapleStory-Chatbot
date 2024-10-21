package com.maple.batch;

import com.maple.service.EventService;
import org.springframework.stereotype.Component;

/**
 * 진행 중인 이벤트 관련 작업을 수행하는 Tasklet 클래스
 */
@Component
public class EventTasklet extends BaseTasklet<EventService> {
    /**
     * 생성자 메서드
     * @param eventService EventService 객체
     */
    public EventTasklet(EventService eventService) {
        super(eventService);
    }

    /**
     * 진행 중인 이벤트 정보 갱신 메서드
     */
    @Override
    protected void fetchData() {
        informationService.fetchEvents();
    }
}
