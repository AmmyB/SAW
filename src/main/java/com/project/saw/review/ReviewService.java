package com.project.saw.review;

import com.project.saw.dto.review.CreateReviewRequest;
import com.project.saw.event.Event;
import com.project.saw.event.EventRepository;
import com.project.saw.exception.ExceptionMessage;
import com.project.saw.user.User;
import com.project.saw.user.UserRepository;
import com.project.saw.utils.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
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

    public Page<Review> getUserReviews(Pageable pageable, Long userId) {
        return reviewRepository.sortedListOfUserReviews(pageable, userId);
    }

    @Transactional
    public Review createReview(Long eventId, CreateReviewRequest createReviewRequest) {
        String username = SecurityUtils.getAuthentication().getName();
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.EVEN_NOT_FOUND_ERROR_MESSAGE + eventId));

        User user = userRepository.findByUserNameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException(ExceptionMessage.USERNAME_NOT_FOUND_EXCEPTION_MESSAGE + username));

        if (createReviewRequest == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }

        if (createReviewRequest.getContent() == null || createReviewRequest.getContent().isEmpty()) {
            throw new IllegalArgumentException("Content cannot be null or empty");
        }

        if (createReviewRequest.getRating() < 1 || createReviewRequest.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be from 1 to 5");
        }

        Review review = new Review();
        review.setEvent(event);
        review.setUser(user);
        review.setTitle(createReviewRequest.getTitle());
        review.setContent(createReviewRequest.getContent());
        review.setRating(createReviewRequest.getRating());
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
