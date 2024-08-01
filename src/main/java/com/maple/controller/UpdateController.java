package com.maple.controller;

import com.maple.service.UpdateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/update")
public class UpdateController {
    private final UpdateService updateService;

    // 업데이트 조회
    @PostMapping("/get")
    public ResponseEntity<Object> findAllUpdate() {
        return ResponseEntity.status(HttpStatus.OK).body(updateService.findAllUpdate());
    }
}
