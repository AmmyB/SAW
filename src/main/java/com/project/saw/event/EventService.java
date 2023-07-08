package com.project.saw.event;


import com.project.saw.dto.CreateEventRequest;
import com.project.saw.dto.UpdateEventRequest;
import com.project.saw.dto.UpdateEventResponse;
import com.project.saw.exception.DuplicateException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import java.util.Optional;


@Slf4j
@Service
public class EventService {

    private final EventRepository eventRepository;
    private static final String DUPLICATE_ERROR_MESSAGE = "This event %s already exist";

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<EventEntity> getEventList() {
        return eventRepository.findAll().stream()
                .sorted(Comparator.comparing(EventEntity::getStartingDate))
                .filter(e -> e.getEndingDate().isAfter(LocalDate.now()))
                .toList();
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

    public UpdateEventResponse updateEvent(Long eventId, UpdateEventRequest updateEventRequest) {
        EventEntity eventEntity = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + eventId));

        var optional = Optional.ofNullable(updateEventRequest).isPresent();

        if (optional) {
            eventEntity.setPrice(updateEventRequest.getPrice());
            eventEntity.setStartingDate(updateEventRequest.getStartingDate());
            eventEntity.setEndingDate(updateEventRequest.getEndingDate());
            eventEntity.setDescription(updateEventRequest.getDescription());
        }
        eventEntity = eventRepository.save(eventEntity);

        return eventToEventResponse(eventEntity);
    }

    public UpdateEventResponse eventToEventResponse(EventEntity eventEntity) {
        return new UpdateEventResponse(
                eventEntity.getId(),
                eventEntity.getTitle(),
                eventEntity.getLocation(),
                eventEntity.getPrice(),
                eventEntity.getStartingDate(),
                eventEntity.getEndingDate(),
                eventEntity.getDescription()
        );

    }

    public void delete(Long eventId) {
        eventRepository.deleteById(eventId);
    }

    public List<EventEntity> searchEvents(String query){
        return eventRepository.searchByTitleLikeIgnoreCase(query);
    }
}
