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

public abstract class InformationService {
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

    protected HashMap<String, Object> createJsonData() {
        HashMap<String, Object> jsonData = new HashMap<>();
        jsonData.put("version", "2.0");

        HashMap<String, Object> template = new HashMap<>();
        jsonData.put("template", template);

        List<HashMap<String, Object>> outputs = new ArrayList<>();
        template.put("outputs", outputs);

        HashMap<String, Object> simpleText = new HashMap<>();
        outputs.add(simpleText);

        HashMap<String, Object> text = new HashMap<>();
        simpleText.put("simpleText", text);

        return jsonData;
    }

    @SuppressWarnings("unchecked")
    protected HashMap<String, Object> extractSimpleText(HashMap<String, Object> jsonData) {
        HashMap<String, Object> template = (HashMap<String, Object>) jsonData.get("template");

        List<HashMap<String, Object>> outputs = (List<HashMap<String, Object>>) template.get("outputs");

        HashMap<String, Object> simpleText = outputs.get(0);

        return (HashMap<String, Object>) simpleText.get("simpleText");
    }

    protected <T extends Information> StringBuilder createMessage(List<T> informationList) {
        StringBuilder result = new StringBuilder();

        result.append(informationList.get(0).getLocalDateTime().toLocalDate()).append(" 오전 03:00 업데이트").append("\n\n");

        for (T information : informationList) {
            result.append(
                    String.join("\n",
                            "\uD83D\uDCE2 " + information.getTitle(),
                            information.getUrl(),
                            information.getFormattedDate()
                    )
            ).append("\n\n");
        }

        return result;
    }
}
