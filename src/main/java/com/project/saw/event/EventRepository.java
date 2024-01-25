package com.project.saw.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
interface EventRepository extends JpaRepository<Event, Long> {


    Optional<Event> findByTitleIgnoreCase(String title);

    @Query("SELECT e FROM Event e WHERE e.title LIKE %:query%")
    List<Event> searchByTitleLikeIgnoreCase(@Param("query") String query);

    @Query("SELECT e FROM Event e WHERE e.endingDate >= now() ORDER BY e.startingDate ASC")
    Page<Event> sortedListOfEvents(Pageable pageable);
}
