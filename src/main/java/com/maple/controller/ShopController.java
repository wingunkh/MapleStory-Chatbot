package com.maple.controller;

import com.maple.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 캐시샵 공지사항 관련 API 요청을 처리하는 컨트롤러 클래스
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/shop")
public class ShopController {
    private final ShopService shopService;

    /**
     * 캐시샵 공지사항 정보를 조회하는 메서드
     * @return 캐시샵 공지사항 정보
     */
    @PostMapping("/get")
    public ResponseEntity<Object> findAllShop() {
        return ResponseEntity.status(HttpStatus.OK).body(shopService.findAllShop());
    }
}
