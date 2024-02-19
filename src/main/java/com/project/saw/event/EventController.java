package com.project.saw.event;


import com.project.saw.dto.event.CreateEventRequest;
import com.project.saw.dto.event.UpdateEventRequest;
import com.project.saw.dto.event.UpdateEventResponse;
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

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@Slf4j
@RestController
@RequestMapping("/event")
class EventController {

    private final EventService eventService;

    @Autowired
    private EventModelAssembler eventModelAssembler;

    @Autowired
    private PagedResourcesAssembler<Event> eventPagedResourcesAssembler;


    public EventController(EventService eventService) {
        this.eventService = eventService;
    }


    @Operation(summary = "Get Event List", description = "Returns a list of all current events")
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Event>>> getEventList(Pageable pageable) {
        log.info("Fetching list of events");
        Page<Event> allEvents = eventService.getEventList(pageable);
        Link link = linkTo(methodOn(EventController.class).getEventList(pageable)).withSelfRel();
        PagedModel<EntityModel<Event>> pagedModel = eventPagedResourcesAssembler.toModel(allEvents,eventModelAssembler);
        return ResponseEntity.ok(pagedModel);
    }

    @Operation(summary = "Create a new event", description = "All parameters are required. Method returns a new event object")
    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody @Valid CreateEventRequest createEventRequest) {
        log.info("Creating an event: {}", createEventRequest);
        Event createEvent = eventService.createEvent(createEventRequest);
        createEvent.add(linkTo(methodOn(EventController.class).createEvent(createEventRequest)).slash(createEvent.getId()).withSelfRel());
        return ResponseEntity.ok(createEvent);
    }

    @Operation(summary = "Update an existing event", description = "All parameters are required. Method returns a updated event object")
    @PatchMapping("{eventId}")
    public ResponseEntity<UpdateEventResponse> updateEvent(@PathVariable @Valid Long eventId, @RequestBody UpdateEventRequest updateEventRequest) {
        log.info("Updating an event with the id: {} by new data: {}", eventId, updateEventRequest);
        UpdateEventResponse updateEvent = eventService.updateEvent(eventId, updateEventRequest);
        updateEvent.add(linkTo(methodOn(EventController.class).updateEvent(eventId,updateEventRequest)).withSelfRel());
        Link link = linkTo(EventController.class).withSelfRel();
        return ResponseEntity.ok(updateEvent);
    }

    @Operation(summary = "Delete an existing event", description = "Event id is required for deletion")
    @DeleteMapping("{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable @Valid Long eventId) {
        log.info("Deleting an event with the id: {}", eventId);
        eventService.delete(eventId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Search for a given event", description = "Searching for an event by a given phrase")
    @GetMapping("/search")
    public ResponseEntity<CollectionModel<Event>> searchEvents(@RequestParam String query) {
        log.info("Searching for an event using a query: {}", query);
        List<Event> searchEvent = eventService.searchEvents(query);
        searchEvent.forEach(event -> event.add(linkTo(methodOn(EventController.class).searchEvents(query)).withSelfRel()));
        Link link = linkTo(EventController.class).withSelfRel();
        return ResponseEntity.ok(CollectionModel.of(searchEvent,link));
    }
}
