package com.maple.controller;

import com.maple.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;

@RestController
@RequiredArgsConstructor
@RequestMapping("/shop")
public class ShopController {
    private final ShopService shopService;

    // 캐시샵 공지사항 조회
    @PostMapping("/get")
    public HashMap<String, Object> findAllShop() {
        return shopService.findAllShop();
    }
}
