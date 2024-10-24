package com.maple.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.HashMap;

/**
 * Controller 클래스의 전역 예외 처리를 위한 서비스 클래스
 */
@Service
@RequiredArgsConstructor
public class ExceptionService extends InformationService {
    /**
     * 카카오톡 챗봇 응답을 위한 JSON 데이터의 "simpleText" key의 value에 오류 메시지를 저장하는 메서드
     * @param errorMessage 오류 메시지
     * @return JSON 데이터
     */
    public HashMap<String, Object> createErrorMessage(String errorMessage) {
        HashMap<String, Object> jsonData = createJsonData();

        HashMap<String, Object> simpleText = extractSimpleText(jsonData);
        simpleText.put("text", errorMessage);

        return jsonData;
    }
}
