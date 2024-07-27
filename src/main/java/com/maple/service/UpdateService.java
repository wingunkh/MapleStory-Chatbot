package com.maple.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.maple.domain.ClientUpdate;
import com.maple.domain.Notice;
import com.maple.repository.UpdateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
public class UpdateService extends InformationService<ClientUpdate> {
    @Value("${api.key}")
    private String key;
    private static final String API_URL = "https://open.api.nexon.com/maplestory/v1/notice-update";
    private final RestTemplate restTemplate;
    private final UpdateRepository updateRepository;

    @Transactional
    public void fetchUpdates() {
        ResponseEntity<String> httpResponse = sendHttpRequest(key, restTemplate, API_URL);

        JsonNode clientUpdateNodes = parseJsonToJsonNode(httpResponse.getBody(), "update_notice");

        List<ClientUpdate> clientUpdates = new ArrayList<>();

        for (JsonNode clientUpdateNode : clientUpdateNodes) {
            ClientUpdate clientUpdate = new ClientUpdate();
            clientUpdate.setTitle(clientUpdateNode.get("title").asText());
            clientUpdate.setUrl(clientUpdateNode.get("url").asText());
            clientUpdate.setDate(Notice.convertTime(String.valueOf(clientUpdateNode.get("date"))));
            clientUpdates.add(clientUpdate);
        }

        updateRepository.saveAll(clientUpdates.stream().limit(10).collect(Collectors.toList()));
    }

    public HashMap<String, Object> findAllUpdate() {
        return parseInformationToJsonString(updateRepository.findAll(), "업데이트");
    }
}
