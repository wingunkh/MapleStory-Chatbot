package com.maple.domain;

import java.time.LocalDateTime;

/**
 * 제공 정보 추상 클래스
 */
public abstract class Information {
    /**
     * 정보 제목 반환 추상 메서드
     * @return 정보 제목
     */
    public abstract String getTitle();

    /**
     * 정보 url 반환 추상 메서드
     * @return 정보 url
     */
    public abstract String getUrl();

    /**
     * 특정 포맷으로 변환된 정보 관련 날짜를 반환하는 추상 메서드
     * @return 포맷된 정보 관련 날짜
     */
    public abstract String getFormattedDate();

    /**
     * 정보 갱신 시각 반환 추상 메서드
     * @return 정보 갱신 시각
     */
    public abstract LocalDateTime getUpdatedDate();
}
