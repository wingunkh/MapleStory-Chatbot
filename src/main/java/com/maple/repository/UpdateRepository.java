package com.maple.repository;

import com.maple.domain.ClientUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UpdateRepository extends JpaRepository<ClientUpdate, Integer> {
}
