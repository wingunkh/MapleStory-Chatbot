package com.maple.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maple.domain.ClientUpdate;
import com.maple.repository.UpdateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UpdateService {
    @Value("${api.key}")
    private String key;
    private static final String API_URL = "https://open.api.nexon.com/maplestory/v1/notice-update";
    private final RestTemplate restTemplate;
    private final UpdateRepository updateRepository;

    @Transactional
    public void fetchUpdates() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("accept", "application/json");
        headers.set("x-nxopen-api-key", key);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(API_URL, HttpMethod.GET, entity, String.class);

        List<ClientUpdate> clientUpdates = parseJsonToUpdates(response.getBody());

        updateRepository.saveAll(clientUpdates.stream().limit(10).collect(Collectors.toList()));
    }

    public HashMap<String, Object> findAllUpdate() {
        return parseUpdatesToJsonString(updateRepository.findAll());
    }

    private List<ClientUpdate> parseJsonToUpdates(String jsonData) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode;

        try {
            rootNode = mapper.readTree(jsonData);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        JsonNode updateNodes = rootNode.get("update_notice");

        List<ClientUpdate> clientUpdates = new ArrayList<>();

        for (JsonNode updateNode : updateNodes) {
            ClientUpdate clientUpdate = new ClientUpdate();
            clientUpdate.setTitle(updateNode.get("title").asText());
            clientUpdate.setUrl(updateNode.get("url").asText());
            clientUpdate.setDate(ClientUpdate.convertTime(String.valueOf(updateNode.get("date"))));
            clientUpdates.add(clientUpdate);
        }

        return clientUpdates;
    }

    private HashMap<String, Object> parseUpdatesToJsonString(List<ClientUpdate> clientUpdates) {
        HashMap<String, Object> jsonString = new HashMap<>();
        jsonString.put("version", "2.0");

        HashMap<String,Object> template = new HashMap<>();
        jsonString.put("template", template);

        List<HashMap<String, Object>> outputs = new ArrayList<>();
        template.put("outputs", outputs);

        HashMap<String, Object> simpleText = new HashMap<>();
        outputs.add(simpleText);

        HashMap<String, Object> text = new HashMap<>();
        simpleText.put("simpleText", text);

        StringBuilder result = new StringBuilder();
        text.put("text", result);

        for (ClientUpdate clientUpdate : clientUpdates) {
            result.append(String.join("\n",
                            "▶ " + clientUpdate.getTitle(),
                            "☞ 업데이트 링크 : " + clientUpdate.getUrl(),
                            "☞ 업데이트 날짜 : " + clientUpdate.getDate()))
                    .append("\n\n");
        }

        return jsonString;
    }
}
