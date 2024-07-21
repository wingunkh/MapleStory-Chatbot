package com.maple.controller;

import com.maple.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notice")
public class NoticeController {
    private final NoticeService noticeService;

    // 공지사항 조회
    @GetMapping
    public ResponseEntity<Object> findAllNotice() {
        return ResponseEntity.status(HttpStatus.OK).body(noticeService.findAllNotice());
    }

    // 공지사항 패치 (for test)
    @GetMapping("/fetch")
    public ResponseEntity<Object> fetchNotices() {
        noticeService.fetchNotices();

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
