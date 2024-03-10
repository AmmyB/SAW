package com.project.saw.review;

import com.project.saw.event.Event;
import com.project.saw.event.EventRepository;
import com.project.saw.exception.ExceptionMessage;
import com.project.saw.user.User;
import com.project.saw.user.UserRepository;
import com.project.saw.utils.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public ReviewService(ReviewRepository reviewRepository, EventRepository eventRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    public Page<Review> getReviewsForEvent(Pageable pageable, Long eventId) {
        return reviewRepository.sortedListOfReviewForEvent(pageable, eventId);
    }

    @Transactional
    public Review createReview(Long eventId, String title, String content, int rating) {
        String username = SecurityUtils.getAuthentication().getName();
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.EVEN_NOT_FOUND_ERROR_MESSAGE + eventId));

        User user = userRepository.findByUserNameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException(ExceptionMessage.USERNAME_NOT_FOUND_EXCEPTION_MESSAGE + username));

        if (content == null || content.isEmpty()) {
            throw new IllegalArgumentException("Content cannot be null or empty");
        }

        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be from 1 to 5");
        }

        Review review = new Review();
        review.setEvent(event);
        review.setUser(user);
        review.setTitle(title);
        review.setContent(content);
        review.setRating(rating);
        review.setCreatedAt(LocalDateTime.now());

        return reviewRepository.save(review);
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.REVIEW_NOT_FOUND_EXCEPTION_MESSAGE + reviewId));

        review.setUser(null);
        review.setEvent(null);

        reviewRepository.delete(review);
    }
}
