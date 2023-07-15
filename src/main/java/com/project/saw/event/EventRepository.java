package com.project.saw.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
interface EventRepository extends JpaRepository<EventEntity, Long> {


    Optional<EventEntity> findByTitleIgnoreCase(String title);

    @Query("SELECT e FROM EventEntity e WHERE e.title LIKE %:query%")
    List<EventEntity> searchByTitleLikeIgnoreCase (@Param("query") String query);
}
