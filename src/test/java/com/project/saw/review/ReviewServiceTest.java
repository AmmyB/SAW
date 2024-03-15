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
import jakarta.persistence.EntityNotFoundException;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;

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

    private static final Review REVIEW = new Review(1L, "Title", "Content", 5,
            LocalDateTime.of(2014, 5, 25, 5, 12, 2, 2), null, null);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        reviewService = new ReviewService(reviewRepository, eventRepository, userRepository);
    }

    @Test
    @DisplayName("Given not empty review list when fetch this list for event then review list should be returned")
    void testGetReviewsForEvent() {
        //given
        var list = List.of(new Review(), new Review());
        Long eventId = 1L;
        Pageable pageable = PageRequest.of(0, 2);
        Page<Review> expectedReviews = new PageImpl<>(list, PageRequest.of(0, 2), list.size());
        when(reviewRepository.sortedListOfReviewForEvent(any(Pageable.class), anyLong())).thenReturn(expectedReviews);
        //when
        Page<Review> result = reviewService.getReviewsForEvent(pageable, eventId);
        //then
        assertEquals(expectedReviews, result);
        verify(reviewRepository).sortedListOfReviewForEvent(pageable, eventId);
    }

    @Test
    @DisplayName("Given non empty list of user reviews when fetch this list then all user reviews should be returned")
    void testGetUserReviews() {
        //given
        var list = List.of(new Review(), new Review());
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 2);
        Page<Review> expectedReviews = new PageImpl<>(list, PageRequest.of(0, 2), list.size());
        when(reviewRepository.sortedListOfUserReviews(any(Pageable.class), eq(userId))).thenReturn(expectedReviews);
        //when
        Page<Review> result = reviewService.getUserReviews(pageable, userId);
        //then
        assertEquals(expectedReviews, result);
        verify(reviewRepository).sortedListOfUserReviews(pageable, userId);
    }

    @Test
    @DisplayName("Given eventId and all required parameters when call createReview method then new review should be created")
    void testCreateReview() {
        //given
        CreateReviewRequest request = new CreateReviewRequest("Title", "Content", 5);
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

    @Test
    @DisplayName("Given null content when call createReview method then throw IllegalArgumentException")
    void createReview_WithNullContent_ShouldThrowIllegalArgumentException() {
        //given
        CreateReviewRequest request = new CreateReviewRequest("Title", null, 5);
        Event event = new Event();
        User user = new User();
        user.setUserName("username");
        event.setId(1L);
        Mockito.when(authentication.getName()).thenReturn(user.getUserName());
        when(userRepository.findByUserNameIgnoreCase(user.getUserName())).thenReturn(Optional.of(user));
        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        //when and then
        assertThrows(IllegalArgumentException.class, () -> reviewService.createReview(event.getId(), request));
    }

    @Test
    @DisplayName("Given invalid rating when call createReview method then throw IllegalArgumentException")
    void createReview_WithInvalidRating_ShouldThrowIllegalArgumentException() {
        //given
        CreateReviewRequest request = new CreateReviewRequest("Title", "content", 6);
        Event event = new Event();
        User user = new User();
        user.setUserName("username");
        event.setId(1L);
        Mockito.when(authentication.getName()).thenReturn(user.getUserName());
        when(userRepository.findByUserNameIgnoreCase(user.getUserName())).thenReturn(Optional.of(user));
        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        //when anmd then
        assertThrows(IllegalArgumentException.class, () -> reviewService.createReview(event.getId(), request));
    }


    @Test
    @DisplayName("Given non-existing event when call createReview method then throw EntityNotFoundException")
    void createReview_WithNonExistingEvent_ShouldThrowEntityNotFoundException() {
        //given
        when(eventRepository.findById(any())).thenReturn(Optional.empty());
        //when and then
        assertThrows(EntityNotFoundException.class, () -> reviewService.createReview(1L, new CreateReviewRequest()));
    }

    @Test
    @DisplayName("Given non-existing user when call createReview method then throw UsernameNotFoundException")
    void createReview_WithNonExistingUser_ShouldThrowUsernameNotFoundException() {
        //given
        when(eventRepository.findById(any())).thenReturn(Optional.of(new Event()));
        when(userRepository.findByUserNameIgnoreCase(any())).thenReturn(Optional.empty());
        //when and then
        assertThrows(UsernameNotFoundException.class, () -> reviewService.createReview(1L, new CreateReviewRequest()));
    }

    @Test
    @DisplayName("Given reviewId when call deleteReview method then the review and all related info should be deleted")
    void testDeleteReview() {
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

    @Test
    @DisplayName("Given non-existing review when call deleteReview method then throw EntityNotFoundException")
    void deleteReview_WithNonExistingReview_ShouldThrowEntityNotFoundException() {
        //given
        when(reviewRepository.findById(any())).thenReturn(Optional.empty());
        //when and then
        assertThrows(EntityNotFoundException.class, () -> reviewService.deleteReview(1L));
    }
}
