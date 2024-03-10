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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
