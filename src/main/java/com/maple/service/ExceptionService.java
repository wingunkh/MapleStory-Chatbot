package com.maple.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class ExceptionService extends InformationService {
    public HashMap<String, Object> createErrorMessage(String errorMessage) {
        HashMap<String, Object> jsonData = createJsonData();

        HashMap<String, Object> simpleText = extractSimpleText(jsonData);

        simpleText.put("text", errorMessage);

        return jsonData;
    }
}
