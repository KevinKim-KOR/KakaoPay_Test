package com.exam.kakaopay.jpa.repository;

import com.exam.kakaopay.jpa.entity.Sprinkle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SprinkleRepository extends JpaRepository<Sprinkle, String> {

    Sprinkle findByToken(String token);

    List<Sprinkle> findListByToken(String token);

    Sprinkle findByTokenAndCreatedAtGreaterThan(String token, LocalDateTime inputDatetime);

    List<Sprinkle> findListByTokenAndCreatedAtGreaterThan(String token, LocalDateTime inputDatetime);
}
