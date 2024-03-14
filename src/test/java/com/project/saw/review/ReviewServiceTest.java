package com.project.saw.review;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.project.saw.dto.review.CreateReviewRequest;
import com.project.saw.event.Event;
import com.project.saw.event.EventRepository;
import com.project.saw.user.User;
import com.project.saw.user.UserRepository;
import com.project.saw.utils.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

class ReviewServiceTest {

    private ReviewService reviewService;

    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private Authentication authentication;

    private static final Review REVIEW = new Review(1L,"Title","Content", 5,
            LocalDateTime.of(2014,5,25,5,12,2,2),null,null);
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        reviewService = new ReviewService(reviewRepository, eventRepository, userRepository);
    }

    @DisplayName("Given not empty review list when fetch this list for event then review list should be returned")
    @Test
    public void testGetReviewsForEvent() {
        //given
        var list = List.of(new Review(),new Review());
        Long eventId = 1L;
        Pageable pageable = PageRequest.of(0,2);
        Page<Review> expectedReviews = new PageImpl<>(list, PageRequest.of(0,2),list.size());
        when(reviewRepository.sortedListOfReviewForEvent(any(Pageable.class),anyLong())).thenReturn(expectedReviews);
        //when
        Page<Review> result = reviewService.getReviewsForEvent(pageable, eventId);
        //then
        assertEquals(expectedReviews, result);
        verify(reviewRepository).sortedListOfReviewForEvent(pageable, eventId);
    }

    @DisplayName("Given non empty list of user reviews when fetch this list then all user reviews should be returned")
    @Test
    public void testGetUserReviews() {
        //given
        var list = List.of(new Review(),new Review());
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0,2);
        Page<Review> expectedReviews = new PageImpl<>(list, PageRequest.of(0,2),list.size());
        when(reviewRepository.sortedListOfUserReviews(any(Pageable.class), userId)).thenReturn(expectedReviews);
        //when
        Page<Review> result = reviewService.getUserReviews(pageable, userId);
        //then
        assertEquals(expectedReviews, result);
        verify(reviewRepository).sortedListOfUserReviews(pageable, userId);
    }

    @DisplayName("Given eventId and all required parameters when call createReview method then new review should be created")
    @Test
    public void testCreateReview() {
        //given
        CreateReviewRequest request = new CreateReviewRequest("Title","Content", 5);
        Long eventId = 1L;
        Event event = new Event();
        User user = new User();
        user.setUserName("username");
        REVIEW.setEvent(event);
        REVIEW.setUser(user);
        Mockito.when(authentication.getName()).thenReturn(user.getUserName());
        when(userRepository.findByUserNameIgnoreCase(user.getUserName())).thenReturn(Optional.of(user));
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        Mockito.when(reviewRepository.save(any())).thenReturn(REVIEW);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        //when
        var result = reviewService.createReview(eventId, request);
        //then
        assertNotNull(result);
        assertEquals(event, result.getEvent());
        assertEquals(user, result.getUser());
        assertEquals(request.getTitle(), result.getTitle());
        assertEquals(request.getContent(), result.getContent());
        assertEquals(request.getRating(), result.getRating());
        assertNotNull(result.getCreatedAt());
        verify(reviewRepository).save(any());
    }

    @DisplayName("Given reviewId when call deleteReview method then the review and all related info should be deleted")
    @Test
    public void testDeleteReview() {
        //given
        Long reviewId = 1L;
        Review review = new Review();
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        //when
        reviewService.deleteReview(reviewId);
        //then
        assertNull(review.getUser());
        assertNull(review.getEvent());
        verify(reviewRepository, times(1)).delete(review);
    }
}
