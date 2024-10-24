package com.maple.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.maple.domain.ClientUpdate;
import com.maple.repository.UpdateRepository;
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
 * 클라이언트 업데이트 정보 제공을 위한 서비스 클래스
 */
@Service
@RequiredArgsConstructor
public class UpdateService extends InformationService {
    @Value("${api.key}")
    private String key;
    private static final String API_URL = "https://open.api.nexon.com/maplestory/v1/notice-update";
    private final RestTemplate restTemplate;
    private final UpdateRepository updateRepository;

    /**
     * 클라이언트 업데이트 정보 갱신 메서드
     */
    @Transactional
    @CacheEvict(value = "myCache", allEntries = true)
    public void fetchUpdates() {
        ResponseEntity<String> httpResponse = sendHttpRequest(key, restTemplate, API_URL);

        JsonNode clientUpdateNodes = parseJsonToJsonNode(httpResponse.getBody(), "update_notice");

        List<ClientUpdate> clientUpdates = new ArrayList<>();

        for (int i = 0; i < Math.min(clientUpdateNodes.size(), 10); i++) {
            JsonNode clientUpdateNode = clientUpdateNodes.get(i);

            ClientUpdate clientUpdate = new ClientUpdate(
                    clientUpdateNode.get("notice_id").asLong(),
                    clientUpdateNode.get("title").asText(),
                    clientUpdateNode.get("url").asText(),
                    convertDate(clientUpdateNode.get("date").asText()),
                    LocalDateTime.now()
            );

            clientUpdates.add(clientUpdate);
        }

        updateRepository.deleteAll();
        updateRepository.saveAll(clientUpdates);
    }

    /**
     * 클라이언트 업데이트 정보 조회 메서드
     * @return JSON 데이터
     */
    @Cacheable(value = "myCache", key = "'update'")
    public HashMap<String, Object> findAllUpdate() {
        HashMap<String, Object> jsonData = createJsonData();

        HashMap<String, Object> simpleText = extractSimpleText(jsonData);

        List<ClientUpdate> clientUpdates = updateRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));

        if (clientUpdates.isEmpty()) {
            throw new RuntimeException();
        }

        simpleText.put("text", createMessage(clientUpdates));

        return jsonData;
    }
}
