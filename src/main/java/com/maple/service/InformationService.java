package com.maple.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maple.domain.Information;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * 정보 제공을 위한 추상 서비스 클래스
 */
public abstract class InformationService {
    /**
     * NEXON API 호출을 위한 HTTP 요청 메서드
     * @param key API 키
     * @param restTemplate RestTemplate 객체
     * @param API_URL 요청할 API URL
     * @return NEXON API 응답 결과
     */
    protected ResponseEntity<String> sendHttpRequest(String key, RestTemplate restTemplate, String API_URL) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("accept", "application/json");
        headers.set("x-nxopen-api-key", key);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(API_URL, HttpMethod.GET, entity, String.class);
    }

    /**
     * NEXON API로부터 응답받은 JSON 데이터를 JsonNode로 변환 후, 특정 노드를 반환하는 메서드
     * @param jsonData JSON 문자열
     * @param nodeName 노드 이름
     * @return 변환된 JsonNode의 특정 노드 (객체 배열)
     */
    protected JsonNode parseJsonToJsonNode(String jsonData, String nodeName) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode;

        try {
            rootNode = mapper.readTree(jsonData);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return rootNode.get(nodeName);
    }

    /**
     * 카카오톡 챗봇 응답을 위한 JSON 데이터를 생성하는 메서드
     * @return JSON 데이터
     */
    protected HashMap<String, Object> createJsonData() {
        HashMap<String, Object> jsonData = new HashMap<>();
        jsonData.put("version", "2.0");

        HashMap<String, Object> template = new HashMap<>();
        jsonData.put("template", template);

        List<HashMap<String, Object>> outputs = new ArrayList<>();
        template.put("outputs", outputs);

        HashMap<String, Object> simpleText = new HashMap<>();
        outputs.add(simpleText);

        HashMap<String, Object> text = new HashMap<>();
        simpleText.put("simpleText", text);

        return jsonData;
    }

    /**
     * 카카오톡 챗봇 응답을 위한 JSON 데이터에서 "simpleText" key의 value를 추출하는 메서드
     * @param jsonData JSON 데이터
     * @return "simpleText" key의 value
     */
    @SuppressWarnings("unchecked")
    protected HashMap<String, Object> extractSimpleText(HashMap<String, Object> jsonData) {
        HashMap<String, Object> template = (HashMap<String, Object>) jsonData.get("template");

        List<HashMap<String, Object>> outputs = (List<HashMap<String, Object>>) template.get("outputs");

        HashMap<String, Object> output = outputs.get(0);

        return (HashMap<String, Object>) output.get("simpleText");
    }


    /**
     * 카카오톡 챗봇 응답을 위한 JSON 데이터의 "simpleText" key의 value에 저장할 메시지를 생성하는 메서드
     * @param informationList 정보 리스트
     * @return 메시지
     * @param <T> Information 타입의 하위 타입
     */
    protected <T extends Information> String createMessage(List<T> informationList) {
        StringBuilder message = new StringBuilder();

        message.append(informationList.get(0).getUpdatedDate().toLocalDate()).append(" 오전 03:00 업데이트").append("\n");
        message.append("(Data based on NEXON Open API)").append("\n\n");

        for (T information : informationList) {
            String formattedInformation = String.join("\n",
                    "\uD83D\uDCE2 " + information.getTitle(),
                    information.getUrl(),
                    information.getFormattedDate()
            );

            message.append(formattedInformation).append("\n\n");
        }

        return message.toString();
    }

    /**
     * OffsetDateTime 문자열을 날짜 + 요일 문자열로 변환하여 반환하는 메서드
     * @param string OffsetDateTime 문자열
     * @return 변환된 날짜 + 요일 문자열 (예: "2024-07-18 (목)")
     */
    public static String convertDate(String string) {
        OffsetDateTime offsetDateTime = OffsetDateTime.parse(string, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        // 문자열 → OffsetDateTime 파싱
        // ex) "2024-07-18T17:30+09:00" → 2024-07-18T17:30+09:00

        LocalDate localDate = offsetDateTime.toLocalDate();
        // OffsetDateTime → LocalDate 변환
        // ex) 2024-07-18T17:30+09:00 → 2024-07-18

        String shortDayOfWeek = localDate.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREAN);
        // 요일 추출
        // ex) 목

        return localDate + " (" + shortDayOfWeek + ")";
        // ex) 2024-07-18 (목)
    }
}
