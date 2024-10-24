package com.maple.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 진행 중인 이벤트 정보 엔티티 클래스
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Event extends Information {
    @Id
    private Long id;

    private String title;

    private String url;

    private String formattedDate;

    private LocalDateTime updatedDate;
}
