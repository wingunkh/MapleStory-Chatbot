package com.maple.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Data
public class Notice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String url;

    private Integer notice_id;

    private LocalDate date;

    // OffsetDateTime → LocalDate 변환
    public static LocalDate convertTime(String string) {
        string = string.substring(1, string.length() - 1);
        // String.valueOf() 메서드로 인한 작은 따옴표 제거

        OffsetDateTime offsetDateTime = OffsetDateTime.parse(string, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        // 문자열 → OffsetDateTime 파싱

        return offsetDateTime.toLocalDate();
        // OffsetDateTime → LocalDate 변환
        // ex) 2024-07-18T17:30+09:00 → 2024-07-18
    }
}
