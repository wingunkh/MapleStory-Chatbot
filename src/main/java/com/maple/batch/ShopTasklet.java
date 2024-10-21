package com.maple.batch;

import com.maple.service.ShopService;
import org.springframework.stereotype.Component;

/**
 * 캐시샵 공시사항 관련 작업을 수행하는 Tasklet 클래스
 */
@Component
public class ShopTasklet extends BaseTasklet<ShopService> {
    /**
     * 생성자 메서드
     * @param shopService ShopService 객체
     */
    public ShopTasklet(ShopService shopService) {
        super(shopService);
    }

    /**
     * 캐시샵 공지사항 정보 갱신 메서드
     */
    @Override
    protected void fetchData() {
        informationService.fetchShops();
    }
}
