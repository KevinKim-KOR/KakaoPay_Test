package com.exam.kakaopay.jpa.repository;

import com.exam.kakaopay.jpa.entity.Sprinkle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface SprinkleRepository extends JpaRepository<Sprinkle, String> {

    Sprinkle findByToken(String token);

    Sprinkle findByTokenAndCreatedAtGreaterThan(String token, LocalDateTime createdAt);

}
