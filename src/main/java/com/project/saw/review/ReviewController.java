package com.project.saw.review;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@RestController
@RequestMapping("/review")
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    private ReviewModelAssembler reviewModelAssembler;
    @Autowired
    private PagedResourcesAssembler<Review> reviewPagedResourcesAssembler;

    private ReviewController(ReviewService reviewService){
        this.reviewService = reviewService;
    }

    @Operation(summary = "Get Review List for Event", description = "Returns a list of all reviews for given event")
    @GetMapping("/{eventId}/reviews")
    public ResponseEntity<CollectionModel<EntityModel<Review>>> getReviewListForEvent(Pageable pageable, @PathVariable @Valid Long eventId){
        log.info("Fetching list of reviews for a given event");
        Page<Review> allReviews = reviewService.getReviewsForEvent(pageable,eventId);
        Link link = linkTo(methodOn(ReviewController.class).getReviewListForEvent(pageable,eventId)).withSelfRel();
        PagedModel<EntityModel<Review>> pagedModel = reviewPagedResourcesAssembler.toModel(allReviews,reviewModelAssembler);
        return ResponseEntity.ok(pagedModel);
    }

    @Operation(summary = "Delete an existing review and its associated data.", description = "Review id is required for deletion. " +
            "The method removes the review and its associated data: " +
            "deletes the review information in the Users and Events tables.")
    @DeleteMapping("{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId){
        log.info("Deleting a review with the id: {}", reviewId);
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }
}
