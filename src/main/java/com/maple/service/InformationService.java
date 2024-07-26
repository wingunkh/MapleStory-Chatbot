package com.maple.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maple.domain.Information;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class InformationService<T extends Information> {
    protected ResponseEntity<String> sendHttpRequest(String key, RestTemplate restTemplate, String API_URL) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("accept", "application/json");
        headers.set("x-nxopen-api-key", key);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(API_URL, HttpMethod.GET, entity, String.class);
    }

    protected JsonNode parseJsonToJsonNode(String jsonData, String rootNodeName) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode;

        try {
            rootNode = mapper.readTree(jsonData);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return rootNode.get(rootNodeName);
    }

    protected HashMap<String, Object> parseInformationToJson(List<T> informationList, String info) {
        HashMap<String, Object> jsonString = new HashMap<>();
        jsonString.put("version", "2.0");

        HashMap<String, Object> template = new HashMap<>();
        jsonString.put("template", template);

        List<HashMap<String, Object>> outputs = new ArrayList<>();
        template.put("outputs", outputs);

        HashMap<String, Object> simpleText = new HashMap<>();
        outputs.add(simpleText);

        HashMap<String, Object> text = new HashMap<>();
        simpleText.put("simpleText", text);

        StringBuilder result = new StringBuilder();
        text.put("text", result);

        for (T information : informationList) {
            result.append(
                    String.join("\n",
                            "▶ " + information.getTitle(),
                            "☞ " + info + " 링크: " + information.getUrl(),
                            "☞ " + info + " 날짜: " + information.getDate()
                    ) // 오버라이드된 메서드가 호출됨
            ).append("\n\n");
        }

        return jsonString;
    }
}
