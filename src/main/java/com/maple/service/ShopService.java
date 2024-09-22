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

@Service
@RequiredArgsConstructor
public class ShopService extends InformationService {
    @Value("${api.key}")
    private String key;
    private static final String API_URL = "https://open.api.nexon.com/maplestory/v1/notice-cashshop";
    private final RestTemplate restTemplate;
    private final ShopRepository shopRepository;

    @Transactional
    @CacheEvict(value = "myCache", allEntries = true)
    public void fetchShops() {
        ResponseEntity<String> httpResponse = sendHttpRequest(key, restTemplate, API_URL);

        JsonNode shopNodes = parseJsonToJsonNode(httpResponse.getBody(), "cashshop_notice");

        List<Shop> shops = new ArrayList<>();

        for (int i = 0; i < Math.min(shopNodes.size(), 10); i++) {
            JsonNode shopNode = shopNodes.get(i);
            Shop shop = new Shop();
            shop.setId(shopNode.get("notice_id").asLong());
            shop.setTitle(shopNode.get("title").asText());
            shop.setUrl(shopNode.get("url").asText());

            String start = String.valueOf(shopNode.get("date_sale_start"));
            String end = String.valueOf(shopNode.get("date_sale_end"));

            if (isNull(start, end)) {
                shop.setStartDate("상시");
                shop.setEndDate("상시");
            } else {
                shop.setStartDate(Shop.convertTime(start));
                shop.setEndDate(Shop.convertTime(end));
            }

            shop.setLocalDateTime(LocalDateTime.now());
            shops.add(shop);
        }

        shopRepository.deleteAll();
        shopRepository.saveAll(shops);
    }

    @Cacheable(value = "myCache", key = "'shop'")
    public HashMap<String, Object> findAllShop() {
        HashMap<String, Object> jsonData = createJsonData();

        HashMap<String, Object> simpleText = extractSimpleText(jsonData);

        StringBuilder result = new StringBuilder();

        List<Shop> shops = shopRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));

        if (shops.isEmpty()) {
            throw new RuntimeException();
        }

        result.append(shops.get(0).getLocalDateTime().toLocalDate()).append(" 오전 03:00 업데이트").append("\n\n");

        for (Shop shop : shops) {
            result.append(
                    String.join("\n",
                            "\uD83D\uDCE2 " + shop.getTitle(),
                            shop.getUrl(),
                            shop.getStartDate() + " ~ " + shop.getEndDate()
                    )
            ).append("\n\n");
        }

        simpleText.put("text", result.toString());

        return jsonData;
    }

    private Boolean isNull(String start, String end) {
        if (start.equals("null") && end.equals("null")) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }
}
