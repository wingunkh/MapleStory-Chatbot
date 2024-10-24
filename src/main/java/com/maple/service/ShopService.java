package com.maple.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.maple.domain.Shop;
import com.maple.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 캐시샵 공지사항 정보 제공을 위한 서비스 클래스
 */
@Service
@RequiredArgsConstructor
public class ShopService extends InformationService {
    @Value("${api.key}")
    private String key;
    private static final String API_URL = "https://open.api.nexon.com/maplestory/v1/notice-cashshop";
    private final RestTemplate restTemplate;
    private final ShopRepository shopRepository;

    /**
     * 캐시샵 공지사항 정보 갱신 메서드
     */
    @Transactional
    @CacheEvict(value = "myCache", allEntries = true)
    public void fetchShops() {
        ResponseEntity<String> httpResponse = sendHttpRequest(key, restTemplate, API_URL);

        JsonNode shopNodes = parseJsonToJsonNode(httpResponse.getBody(), "cashshop_notice");

        List<Shop> shops = new ArrayList<>();

        for (int i = 0; i < Math.min(shopNodes.size(), 10); i++) {
            JsonNode shopNode = shopNodes.get(i);

            Shop shop = new Shop(
                    shopNode.get("notice_id").asLong(),
                    shopNode.get("title").asText(),
                    shopNode.get("url").asText(),
                    formatSaleDate(shopNode.get("date_sale_start").asText(), shopNode.get("date_sale_end").asText()),
                    LocalDateTime.now()
            );

            shops.add(shop);
        }

        shopRepository.deleteAll();
        shopRepository.saveAll(shops);
    }

    /**
     * 캐시샵 공지사항 정보 조회 메서드
     * @return JSON 데이터
     */
    @Cacheable(value = "myCache", key = "'shop'")
    public HashMap<String, Object> findAllShop() {
        HashMap<String, Object> jsonData = createJsonData();

        HashMap<String, Object> simpleText = extractSimpleText(jsonData);

        List<Shop> shops = shopRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));

        if (shops.isEmpty()) {
            throw new RuntimeException();
        }

        simpleText.put("text", createMessage(shops));

        return jsonData;
    }

    /**
     * 판매 날짜를 포맷하는 메서드
     *
     * @param start 판매 시작 날짜
     * @param end 판매 종료 날짜
     * @return 포맷된 판매 날짜
     */
    private String formatSaleDate(String start, String end) {
        if (start.equals("null") && end.equals("null")) {
            return "상시 ~ 상시";
        }

        return convertDate(start) + "~" + convertDate(end);
    }
}
