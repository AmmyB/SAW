package com.project.saw.review;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review,Long> {

    @Query("SELECT r FROM Review r WHERE r.event.id = :eventId order by r.createdAt DESC")
    Page<Review> sortedListOfReviewForEvent(Pageable pageable, Long eventId);


}
