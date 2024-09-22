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

        for (int i = 0; i < Math.min(noticeNodes.size(), 10); i++) {
            JsonNode noticeNode = noticeNodes.get(i);
            Notice notice = new Notice();
            notice.setId(noticeNode.get("notice_id").asLong());
            notice.setTitle(noticeNode.get("title").asText());
            notice.setUrl(noticeNode.get("url").asText());
            notice.setDate(Notice.convertTime(String.valueOf(noticeNode.get("date"))));
            notice.setLocalDateTime(LocalDateTime.now());
            notices.add(notice);
        }

        noticeRepository.deleteAll();
        noticeRepository.saveAll(notices);
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

        result.append(notices.get(0).getLocalDateTime().toLocalDate()).append(" 오전 03:00 업데이트").append("\n\n");

        for (Notice notice : notices) {
            result.append(
                    String.join("\n",
                            "\uD83D\uDCE2 " + notice.getTitle(),
                            notice.getUrl(),
                            notice.getDate()
                    )
            ).append("\n\n");
        }

        simpleText.put("text", result.toString());

        return jsonData;
    }
}
