package com.maple.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Shop extends Information {
    @Id
    private Long id;

    private String title;

    private String url;

    private String startDate;

    private String endDate;
}
