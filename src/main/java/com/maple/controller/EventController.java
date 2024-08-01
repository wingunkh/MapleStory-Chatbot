package com.maple.controller;

import com.maple.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/event")
public class EventController {
    private final EventService eventService;

    // 이벤트 조회
    @PostMapping("/get")
    public ResponseEntity<Object> findAllEvent() {
        return ResponseEntity.status(HttpStatus.OK).body(eventService.findAllEvent());
    }
}
