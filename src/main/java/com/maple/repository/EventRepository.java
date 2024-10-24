package com.maple.repository;

import com.maple.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Event 엔티티에 대한 데이터베이스 접근을 제공하는 리포지토리 인터페이스
 * - Spring Data JPA의 JpaRepository를 상속하여 기본 CRUD 메서드를 자동으로 제공
 */
@Repository
public interface EventRepository extends JpaRepository<Event, Integer> { }
