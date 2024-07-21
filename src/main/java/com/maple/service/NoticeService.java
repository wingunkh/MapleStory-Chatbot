package com.maple.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maple.domain.Notice;
import com.maple.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeService {
    @Value("${api.key}")
    private String key;
    private static final String API_URL = "https://open.api.nexon.com/maplestory/v1/notice";
    private final RestTemplate restTemplate;
    private final NoticeRepository noticeRepository;

    public void fetchNotices() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("accept", "application/json");
        headers.set("x-nxopen-api-key", key);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(API_URL, HttpMethod.GET, entity, String.class);

        List<Notice> notices = parseJsonToNotices(response.getBody());

        noticeRepository.saveAll(notices);
    }

    public List<Notice> findAllNotice() {
        return noticeRepository.findAll();
    }

    public List<Notice> parseJsonToNotices(String jsonData) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode;

        try {
            rootNode = mapper.readTree(jsonData);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        JsonNode noticeNodes = rootNode.get("notice");

        List<Notice> notices = new ArrayList<>();

        for (JsonNode noticeNode : noticeNodes) {
            Notice notice = new Notice();
            notice.setTitle(noticeNode.get("title").asText());
            notice.setUrl(noticeNode.get("url").asText());
            notice.setNotice_id(noticeNode.get("notice_id").asInt());
            notice.setDate(Notice.convertTime(String.valueOf(noticeNode.get("date"))));
            notices.add(notice);
        }

        return notices;
    }
}
