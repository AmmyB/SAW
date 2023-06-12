package com.project.saw.event;


import com.project.saw.dto.CreateEventRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<EventEntity> getAll() {
        return eventRepository.findAll();
    }

    public EventEntity createEvent(CreateEventRequest request) {
        EventEntity eventEntity = EventEntity.builder()
                .title(request.getTitle())
                .location(request.getLocation())
                .price(request.getPrice())
                .startingDate(request.getStartingDate())
                .endingDate(request.getEndingDate())
                .description(request.getDescription())
                .build();

        return eventRepository.save(eventEntity);
    }

}
