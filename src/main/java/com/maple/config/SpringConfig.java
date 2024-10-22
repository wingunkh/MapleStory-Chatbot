package com.maple.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Spring 설정 클래스
 */
@Configuration
public class SpringConfig {
    /**
     * 외부 REST API 호출을 위한 RestTemplate 빈을 생성하는 메서드
     * @return RestTemplate 객체
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
