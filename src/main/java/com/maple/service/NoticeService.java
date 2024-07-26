package com.maple.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.maple.domain.Notice;
import com.maple.repository.NoticeRepository;
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
public class NoticeService extends InformationService<Notice> {
    @Value("${api.key}")
    private String key;
    private static final String API_URL = "https://open.api.nexon.com/maplestory/v1/notice";
    private final RestTemplate restTemplate;
    private final NoticeRepository noticeRepository;

    @Transactional
    public void fetchNotices() {
        ResponseEntity<String> httpResponse = sendHttpRequest(key, restTemplate, API_URL);

        JsonNode noticeNodes = parseJsonToJsonNode(httpResponse.getBody(), "notice");

        List<Notice> notices = new ArrayList<>();

        for (JsonNode noticeNode : noticeNodes) {
            Notice notice = new Notice();
            notice.setTitle(noticeNode.get("title").asText());
            notice.setUrl(noticeNode.get("url").asText());
            notice.setDate(Notice.convertTime(String.valueOf(noticeNode.get("date"))));
            notices.add(notice);
        }

        noticeRepository.saveAll(notices.stream().limit(10).collect(Collectors.toList()));
    }

    public HashMap<String, Object> findAllNotice() {
        return parseInformationToJson(noticeRepository.findAll(), "공지");
    }
}
