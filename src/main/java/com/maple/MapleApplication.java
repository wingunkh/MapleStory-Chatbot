package com.maple;

import com.maple.service.EventService;
import com.maple.service.NoticeService;
import com.maple.service.UpdateService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
public class MapleApplication {
	private final NoticeService noticeService;
	private final UpdateService updateService;
	private final EventService eventService;

	public static void main(String[] args) {
		SpringApplication.run(MapleApplication.class, args);
	}

	// 스프링 빈의 이벤트 라이프사이클
	// 스프링 컨테이너 생성 → 스프링 빈 생성 → 의존관계 주입 → 초기화 콜백 → 사용 → 소멸 전 콜백 → 스프링 종료
	// @PostConstruct : 초기화 콜백 (빈이 생성되고, 빈의 의존관계 주입이 완료된 후 호출)
	// @PreDestroy : 소멸 전 콜백 (빈이 소멸되기 직전에 호출)
	@PostConstruct
	public void init() {
//		noticeService.fetchNotices();
//		updateService.fetchUpdates();
//		eventService.fetchEvents();
	}
}
