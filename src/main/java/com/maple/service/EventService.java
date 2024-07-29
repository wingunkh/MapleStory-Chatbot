package com.maple.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.maple.domain.Event;
import com.maple.repository.EventRepository;
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
public class EventService extends InformationService {
    @Value("${api.key}")
    private String key;
    private static final String API_URL = "https://open.api.nexon.com/maplestory/v1/notice-event";
    private final RestTemplate restTemplate;
    private final EventRepository eventRepository;

    @Transactional
    public void fetchEvents() {
        ResponseEntity<String> httpResponse = sendHttpRequest(key, restTemplate, API_URL);

        JsonNode eventNodes = parseJsonToJsonNode(httpResponse.getBody(), "event_notice");

        List<Event> events = new ArrayList<>();

        for (JsonNode eventNode : eventNodes) {
            Event event = new Event();
            event.setTitle(eventNode.get("title").asText());
            event.setTitle(eventNode.get("title").asText());
            event.setUrl(eventNode.get("url").asText());
            event.setStartDate(Event.convertTime(String.valueOf(eventNode.get("date_event_start"))));
            event.setEndDate(Event.convertTime(String.valueOf(eventNode.get("date_event_end"))));
            events.add(event);
        }

        eventRepository.saveAll(events);
    }

    public HashMap<String, Object> findAllEvent() {
        HashMap<String, Object> jsonString = createJsonTemplate();

        HashMap<String, Object> simpleText = extractSimpleText(jsonString);

        StringBuilder result = new StringBuilder();

        for (Event event : eventRepository.findAll()) {
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

        return jsonString;
    }
}
