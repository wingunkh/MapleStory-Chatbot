package com.maple.controller;

import com.maple.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 진행 중인 이벤트 관련 API 요청을 처리하는 컨트롤러 클래스
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/event")
public class EventController {
    private final EventService eventService;

    /**
     * 진행 중인 이벤트 정보를 조회하는 메서드
     * @return 진행 중인 이벤트 정보
     */
    @PostMapping("/get")
    public ResponseEntity<Object> findAllEvent() {
        return ResponseEntity.status(HttpStatus.OK).body(eventService.findAllEvent());
    }
}
