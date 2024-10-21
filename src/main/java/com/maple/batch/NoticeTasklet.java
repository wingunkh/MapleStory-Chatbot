package com.maple.batch;

import com.maple.service.NoticeService;
import org.springframework.stereotype.Component;

/**
 * 공지사항 관련 작업을 수행하는 Tasklet 클래스
 */
@Component
public class NoticeTasklet extends BaseTasklet<NoticeService> {
    /**
     * 생성자 메서드
     * @param noticeService NoticeService 객체
     */
    public NoticeTasklet(NoticeService noticeService) {
        super(noticeService);
    }

    /**
     * 공지사항 정보 갱신 메서드
     */
    @Override
    protected void fetchData() {
        informationService.fetchNotices();
    }
}
