package com.maple.controller;

import com.maple.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notice")
public class NoticeController {
    private final NoticeService noticeService;

    // 공지사항 조회
    @PostMapping("/get")
    public ResponseEntity<Object> findAllNotice() {
        return ResponseEntity.status(HttpStatus.OK).body(noticeService.findAllNotice());
    }
}
