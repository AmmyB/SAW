package com.project.saw.review;

import com.project.saw.event.EventRepository;
import com.project.saw.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final EventRepository eventRepository;
    private UserRepository userRepository;

    public ReviewService(ReviewRepository reviewRepository, EventRepository eventRepository, UserRepository userRepository){
        this.reviewRepository = reviewRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    public Page<Review> getReviewsForEvent(Pageable pageable,Long eventId){
        return reviewRepository.sortedListOfReviewForEvent(pageable,eventId);
    }
}
