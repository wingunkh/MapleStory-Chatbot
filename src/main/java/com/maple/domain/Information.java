package com.maple.domain;

import lombok.Data;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

@Data
public abstract class Information {
    private Long id;

    private String title;

    private String url;

    private String date;

    // OffsetDateTime → LocalDate + (요일) 변환
    public static String convertTime(String string) {
        string = string.substring(1, string.length() - 1);
        // String.valueOf() 메서드로 인한 작은 따옴표 제거

        OffsetDateTime offsetDateTime = OffsetDateTime.parse(string, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        // 문자열 → OffsetDateTime 파싱

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
