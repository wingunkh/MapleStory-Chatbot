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

        for (int i = 0; i < Math.min(eventNodes.size(), 10); i++) {
            JsonNode eventNode = eventNodes.get(i);
            Event event = new Event();
            event.setId(eventNode.get("notice_id").asLong());
            event.setTitle(eventNode.get("title").asText());
            event.setUrl(eventNode.get("url").asText());
            event.setStartDate(Event.convertTime(String.valueOf(eventNode.get("date_event_start"))));
            event.setEndDate(Event.convertTime(String.valueOf(eventNode.get("date_event_end"))));
            event.setLocalDateTime(LocalDateTime.now());
            events.add(event);
        }

        eventRepository.deleteAll();
        eventRepository.saveAll(events);
    }

    @Cacheable(value = "myCache", key = "'event'")
    public HashMap<String, Object> findAllEvent() {
        HashMap<String, Object> jsonData = createJsonData();

        HashMap<String, Object> simpleText = extractSimpleText(jsonData);

        List<Event> events = eventRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));

        if (events.isEmpty()) {
            throw new RuntimeException();
        }

        simpleText.put("text", createMessage(events).toString());

        return jsonData;
    }
}
