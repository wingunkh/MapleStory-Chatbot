package com.maple.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.maple.domain.Notice;
import com.maple.repository.NoticeRepository;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoticeService extends InformationService {
    @Value("${api.key}")
    private String key;
    private static final String API_URL = "https://open.api.nexon.com/maplestory/v1/notice";
    private final RestTemplate restTemplate;
    private final NoticeRepository noticeRepository;

    @Transactional
    @CacheEvict(value = "myCache", allEntries = true)
    public void fetchNotices() {
        ResponseEntity<String> httpResponse = sendHttpRequest(key, restTemplate, API_URL);

        JsonNode noticeNodes = parseJsonToJsonNode(httpResponse.getBody(), "notice");

        List<Notice> notices = new ArrayList<>();

        for (JsonNode noticeNode : noticeNodes) {
            Notice notice = new Notice();
            notice.setId(noticeNode.get("notice_id").asLong());
            notice.setTitle(noticeNode.get("title").asText());
            notice.setUrl(noticeNode.get("url").asText());
            notice.setDate(Notice.convertTime(String.valueOf(noticeNode.get("date"))));
            notice.setLocalDateTime(LocalDateTime.now());
            notices.add(notice);
        }

        noticeRepository.saveAll(notices.stream().limit(10).collect(Collectors.toList()));
    }

    @Cacheable(value = "myCache", key = "'notice'")
    public HashMap<String, Object> findAllNotice() {
        HashMap<String, Object> jsonData = createJsonData();

        HashMap<String, Object> simpleText = extractSimpleText(jsonData);

        StringBuilder result = new StringBuilder();

        List<Notice> notices = noticeRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));

        if (notices.isEmpty()) {
            throw new RuntimeException();
        }

        for (Notice notice : notices) {
            result.append(
                    String.join("\n",
                            "▶ " + notice.getTitle(),
                            "☞ 공지 링크: " + notice.getUrl(),
                            "☞ 공지 날짜: " + notice.getDate()
                    )
            ).append("\n\n");
        }

        simpleText.put("text", result.toString());

        return jsonData;
    }
}
