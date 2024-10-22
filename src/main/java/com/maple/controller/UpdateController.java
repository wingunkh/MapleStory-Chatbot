package com.maple.controller;

import com.maple.service.UpdateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 클라이언트 업데이트 관련 API 요청을 처리하는 컨트롤러 클래스
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/update")
public class UpdateController {
    private final UpdateService updateService;

    /**
     * 클라이언트 업데이트 정보를 조회하는 메서드
     * @return 클라이언트 업데이트 정보
     */
    @PostMapping("/get")
    public ResponseEntity<Object> findAllUpdate() {
        return ResponseEntity.status(HttpStatus.OK).body(updateService.findAllUpdate());
    }
}
