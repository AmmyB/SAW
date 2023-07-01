package com.project.saw.event;


import com.project.saw.dto.CreateEventRequest;
import com.project.saw.dto.UpdateEventRequest;
import com.project.saw.dto.UpdateEventResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/event")
class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public List<EventEntity> getEventList(){
        return eventService.getAll();
    }

    @PostMapping
    public EventEntity createEvent(@RequestBody CreateEventRequest createEventRequest){
        log.info("Creating an event: {}", createEventRequest);
      return eventService.createEvent(createEventRequest);

    }

    @PatchMapping("{eventId}")
    public UpdateEventResponse updateEvent(@PathVariable Long eventId, @RequestBody UpdateEventRequest updateEventRequest){
        log.info("Updating an event with the id: {} by new data: {}", eventId, updateEventRequest);
        return eventService.updateEvent(eventId, updateEventRequest);
    }
}
