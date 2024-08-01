package com.maple.controller;

import com.maple.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/shop")
public class ShopController {
    private final ShopService shopService;

    // 캐시샵 공지사항 조회
    @PostMapping("/get")
    public ResponseEntity<Object> findAllShop() {
        return ResponseEntity.status(HttpStatus.OK).body(shopService.findAllShop());
    }
}
