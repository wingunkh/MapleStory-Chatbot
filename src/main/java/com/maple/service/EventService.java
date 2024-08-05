package com.maple.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.maple.domain.Event;
import com.maple.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
public class EventService extends InformationService {
    @Value("${api.key}")
    private String key;
    private static final String API_URL = "https://open.api.nexon.com/maplestory/v1/notice-event";
    private final RestTemplate restTemplate;
    private final EventRepository eventRepository;

    @Transactional
    @CacheEvict(value = "myCache", allEntries = true)
    public void fetchEvents() {
        ResponseEntity<String> httpResponse = sendHttpRequest(key, restTemplate, API_URL);

        JsonNode eventNodes = parseJsonToJsonNode(httpResponse.getBody(), "event_notice");

        List<Event> events = new ArrayList<>();

        for (JsonNode eventNode : eventNodes) {
            Event event = new Event();
            event.setTitle(eventNode.get("title").asText());
            event.setUrl(eventNode.get("url").asText());
            event.setStartDate(Event.convertTime(String.valueOf(eventNode.get("date_event_start"))));
            event.setEndDate(Event.convertTime(String.valueOf(eventNode.get("date_event_end"))));
            events.add(event);
        }

        eventRepository.saveAll(events.stream().limit(10).collect(Collectors.toList()));
    }

    @Cacheable(value = "myCache", key = "'event'")
    public HashMap<String, Object> findAllEvent() {
        HashMap<String, Object> jsonData = createJsonData();

        HashMap<String, Object> simpleText = extractSimpleText(jsonData);

        StringBuilder result = new StringBuilder();

        List<Event> events = eventRepository.findAll();

        if (events.isEmpty()) {
            throw new RuntimeException();
        }

        for (Event event : events) {
            result.append(
                    String.join("\n",
                            "▶ " + event.getTitle(),
                            "☞ 이벤트 링크: " + event.getUrl(),
                            "☞ 이벤트 시작 날짜: " + event.getStartDate(),
                            "☞ 이벤트 종료 날짜: " + event.getEndDate()
                    )
            ).append("\n\n");
        }

        simpleText.put("text", result.toString());

        return jsonData;
    }
}
