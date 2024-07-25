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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoticeService {
    @Value("${api.key}")
    private String key;
    private static final String API_URL = "https://open.api.nexon.com/maplestory/v1/notice";
    private final RestTemplate restTemplate;
    private final NoticeRepository noticeRepository;

    @Transactional
    public void fetchNotices() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("accept", "application/json");
        headers.set("x-nxopen-api-key", key);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(API_URL, HttpMethod.GET, entity, String.class);

        List<Notice> notices = parseJsonToNotices(response.getBody());

        noticeRepository.saveAll(notices.stream().limit(10).collect(Collectors.toList()));
    }

    public HashMap<String, Object> findAllNotice() {
        return parseNoticesToJsonString(noticeRepository.findAll());
    }

    private List<Notice> parseJsonToNotices(String jsonData) {
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
            notice.setDate(Notice.convertTime(String.valueOf(noticeNode.get("date"))));
            notices.add(notice);
        }

        return notices;
    }

    private HashMap<String, Object> parseNoticesToJsonString(List<Notice> notices) {
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

        for (Notice notice : notices) {
            result.append(String.join("\n",
                    "▶ " + notice.getTitle(),
                    "☞ 공지 링크 : " + notice.getUrl(),
                    "☞ 공지 날짜 : " + notice.getDate()))
                    .append("\n\n");
        }

        return jsonString;
    }
}
