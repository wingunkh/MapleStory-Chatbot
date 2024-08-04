package com.maple;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
public class MapleApplication {
	public static void main(String[] args) {
		SpringApplication.run(MapleApplication.class, args);
	}
}
