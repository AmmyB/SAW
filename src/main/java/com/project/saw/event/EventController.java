package com.project.saw.event;


import com.project.saw.dto.event.CreateEventRequest;
import com.project.saw.dto.event.UpdateEventRequest;
import com.project.saw.dto.event.UpdateEventResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;


@Slf4j
@RestController
@RequestMapping("/event")
class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public CollectionModel<Event> getEventList() {
        List<Event> allEvent = eventService.getEventList();
        allEvent.forEach(eventEntity -> eventEntity.add(linkTo(EventController.class).slash(eventEntity.getId()).withSelfRel()));
        Link link = linkTo(EventController.class).withSelfRel();
        return CollectionModel.of(allEvent, link);
    }

    @PostMapping
    public Event createEvent(@RequestBody @Valid CreateEventRequest createEventRequest) {
        log.info("Creating an event: {}", createEventRequest);
        return eventService.createEvent(createEventRequest);

    }

    @PatchMapping("{eventId}")
    public UpdateEventResponse updateEvent(@PathVariable @Valid Long eventId, @RequestBody UpdateEventRequest updateEventRequest) {
        log.info("Updating an event with the id: {} by new data: {}", eventId, updateEventRequest);
        return eventService.updateEvent(eventId, updateEventRequest);
    }

    @DeleteMapping("{eventId}")
    public void deleteEvent(@PathVariable @Valid Long eventId) {
        log.info("Deleting an event with the id: {}", eventId);
        eventService.delete(eventId);
    }

    @GetMapping("/search")
    public List<Event> searchEvents(@RequestParam String query) {
        return eventService.searchEvents(query);
    }

}
