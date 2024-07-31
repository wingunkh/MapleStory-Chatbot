package com.maple.controller;

import com.maple.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;

@RestController
@RequiredArgsConstructor
@RequestMapping("/event")
@Slf4j
public class EventController {
    private final EventService eventService;

    // 이벤트 조회
    @PostMapping("/get")
    public HashMap<String, Object> findAllEvent() {
        long startTime = System.currentTimeMillis();

        HashMap<String, Object> tmp = eventService.findAllEvent();

        log.info("findAllEvent() Duration: {}ms", System.currentTimeMillis() - startTime);

        return tmp;
    }

    // 이벤트 패치 (for test)
    @PostMapping("/fetch")
    public ResponseEntity<Object> fetchEvents() {
        eventService.fetchEvents();

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
