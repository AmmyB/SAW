package com.project.saw.event;


import com.project.saw.dto.CreateEventRequest;
import com.project.saw.exception.DuplicateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class EventService {

    private final EventRepository eventRepository;
    private static final String DUPLICATE_ERROR_MESSAGE = "This event %s already exist";

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<EventEntity> getAll() {
        return eventRepository.findAll();
    }

    public EventEntity createEvent(CreateEventRequest request) {
        eventRepository.findByTitle(request.getTitle())
                .ifPresent(EventEntity -> {
                    var error = String.format(DUPLICATE_ERROR_MESSAGE, request.getTitle());
                    throw new DuplicateException(error);
                });
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
