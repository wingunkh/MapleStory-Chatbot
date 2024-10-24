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

/**
 * 공지사항 정보 제공을 위한 서비스 클래스
 */
@Service
@RequiredArgsConstructor
public class NoticeService extends InformationService {
    @Value("${api.key}")
    private String key;
    private static final String API_URL = "https://open.api.nexon.com/maplestory/v1/notice";
    private final RestTemplate restTemplate;
    private final NoticeRepository noticeRepository;

    /**
     * 공지사항 정보 갱신 메서드
     */
    @Transactional
    @CacheEvict(value = "myCache", allEntries = true)
    public void fetchNotices() {
        ResponseEntity<String> httpResponse = sendHttpRequest(key, restTemplate, API_URL);

        JsonNode noticeNodes = parseJsonToJsonNode(httpResponse.getBody(), "notice");

        List<Notice> notices = new ArrayList<>();

        for (int i = 0; i < Math.min(noticeNodes.size(), 10); i++) {
            JsonNode noticeNode = noticeNodes.get(i);

            Notice notice = new Notice(
                    noticeNode.get("notice_id").asLong(),
                    noticeNode.get("title").asText(),
                    noticeNode.get("url").asText(),
                    convertDate(noticeNode.get("date").asText()),
                    LocalDateTime.now()
            );

            notices.add(notice);
        }

        noticeRepository.deleteAll();
        noticeRepository.saveAll(notices);
    }

    /**
     * 공지사항 정보 조회 메서드
     * @return JSON 데이터
     */
    @Cacheable(value = "myCache", key = "'notice'")
    public HashMap<String, Object> findAllNotice() {
        HashMap<String, Object> jsonData = createJsonData();

        HashMap<String, Object> simpleText = extractSimpleText(jsonData);

        List<Notice> notices = noticeRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));

        if (notices.isEmpty()) {
            throw new RuntimeException();
        }

        simpleText.put("text", createMessage(notices));

        return jsonData;
    }
}
