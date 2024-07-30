package com.maple.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.maple.domain.Shop;
import com.maple.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
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
    public void fetchShops() {
        ResponseEntity<String> httpResponse = sendHttpRequest(key, restTemplate, API_URL);

        JsonNode shopNodes = parseJsonToJsonNode(httpResponse.getBody(), "cashshop_notice");

        List<Shop> shops = new ArrayList<>();

        for (JsonNode shopNode : shopNodes) {
            Shop shop = new Shop();
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

            shops.add(shop);
        }

        shopRepository.saveAll(shops);
    }

    public HashMap<String, Object> findAllShop() {
        HashMap<String, Object> jsonString = createJsonTemplate();

        HashMap<String, Object> simpleText = extractSimpleText(jsonString);

        StringBuilder result = new StringBuilder();

        for (Shop shop : shopRepository.findAll()) {
            result.append(
                    String.join("\n",
                            "▶ " + shop.getTitle(),
                            "☞ 캐시샵 공지 링크: " + shop.getUrl(),
                            "☞ 판매 시작 날짜: " + shop.getStartDate(),
                            "☞ 판매 종료 날짜: " + shop.getEndDate()
                    )
            ).append("\n\n");
        }

        simpleText.put("text", result.toString());

        return jsonString;
    }

    private Boolean isNull(String start, String end) {
        if (start.equals("null") && end.equals("null")) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }
}
