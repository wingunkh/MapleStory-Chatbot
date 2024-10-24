package com.maple.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.maple.domain.Event;
import com.maple.repository.EventRepository;
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
 * 진행 중인 이벤트 정보 제공을 위한 서비스 클래스
 */
@Service
@RequiredArgsConstructor
public class EventService extends InformationService {
    @Value("${api.key}")
    private String key;
    private static final String API_URL = "https://open.api.nexon.com/maplestory/v1/notice-event";
    private final RestTemplate restTemplate;
    private final EventRepository eventRepository;

    /**
     * 진행 중인 이벤트 정보 갱신 메서드
     */
    @Transactional
    @CacheEvict(value = "myCache", allEntries = true)
    public void fetchEvents() {
        ResponseEntity<String> httpResponse = sendHttpRequest(key, restTemplate, API_URL);

        JsonNode eventNodes = parseJsonToJsonNode(httpResponse.getBody(), "event_notice");

        List<Event> events = new ArrayList<>();

        for (int i = 0; i < Math.min(eventNodes.size(), 10); i++) {
            JsonNode eventNode = eventNodes.get(i);

            Event event = new Event(
                    eventNode.get("notice_id").asLong(),
                    eventNode.get("title").asText(),
                    eventNode.get("url").asText(),
                    formatEventDate(eventNode.get("date_event_start").asText(), eventNode.get("date_event_end").asText()),
                    LocalDateTime.now()
            );

            events.add(event);
        }

        eventRepository.deleteAll();
        eventRepository.saveAll(events);
    }

    /**
     * 진행 중인 이벤트 정보 조회 메서드
     * @return JSON 데이터
     */
    @Cacheable(value = "myCache", key = "'event'")
    public HashMap<String, Object> findAllEvent() {
        HashMap<String, Object> jsonData = createJsonData();

        HashMap<String, Object> simpleText = extractSimpleText(jsonData);

        List<Event> events = eventRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));

        if (events.isEmpty()) {
            throw new RuntimeException();
        }

        simpleText.put("text", createMessage(events));

        return jsonData;
    }

    /**
     * 이벤트 진행 날짜를 포맷하는 메서드
     * @param start 이벤트 시작 날짜
     * @param end 이벤트 종료 날짜
     * @return 포맷된 이벤트 진행 날짜
     */
    private String formatEventDate(String start, String end) {
        return convertDate(start) + "~" + convertDate(end);
    }
}
