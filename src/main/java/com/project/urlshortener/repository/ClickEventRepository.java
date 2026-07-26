package com.project.urlshortener.repository;

import com.project.urlshortener.entity.ClickEvent;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface ClickEventRepository extends JpaRepository<ClickEvent, Long> {

    @EntityGraph(attributePaths = {"shortUrl", "createdBy"})
    List<ClickEvent> findByClickedAtBetweenOrderByClickedAtAsc(Instant start, Instant end);
}
