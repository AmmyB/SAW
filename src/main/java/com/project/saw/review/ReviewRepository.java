package com.project.saw.review;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ReviewRepository extends JpaRepository<Review,Long> {

    @Transactional(readOnly = true)
    @Query("SELECT r FROM Review r WHERE r.event.id = :eventId order by r.createdAt DESC")
    Page<Review> sortedListOfReviewForEvent(Pageable pageable, Long eventId);

    @Transactional(readOnly = true)
    @Query("SELECT r FROM Review r WHERE r.user.id = :userId order by r.createdAt DESC")
    Page<Review> sortedListOfUserReviews(Pageable pageable, Long userId);

}
