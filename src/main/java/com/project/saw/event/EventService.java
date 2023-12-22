package com.project.saw.event;


import com.project.saw.dto.event.CreateEventRequest;
import com.project.saw.dto.event.UpdateEventRequest;
import com.project.saw.dto.event.UpdateEventResponse;
import com.project.saw.exception.DuplicateException;
import com.project.saw.exception.ExceptionMessage;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import java.util.Optional;


@Slf4j
@Service
public class EventService {

    private final EventRepository eventRepository;


    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<EventEntity> getEventList() {
        return eventRepository.sortedListOfEvents(Sort.by("startingDate")).stream()
                .filter(event -> event.getEndingDate().isAfter(LocalDate.now()))
                .toList();
    }

    public EventEntity createEvent(CreateEventRequest request) {
        eventRepository.findByTitleIgnoreCase(request.getTitle())
                .ifPresent(EventEntity -> {
                    var error = String.format(ExceptionMessage.DUPLICATE_EVENT_ERROR_MESSAGE, request.getTitle());
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
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.EVEN_NOT_FOUND_ERROR_MESSAGE + eventId));

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

    private UpdateEventResponse eventToEventResponse(EventEntity eventEntity) {
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
        EventEntity eventToDelete = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.EVEN_NOT_FOUND_ERROR_MESSAGE + eventId));
        eventRepository.delete(eventToDelete);

    }

    public List<EventEntity> searchEvents(String query) {
        return eventRepository.searchByTitleLikeIgnoreCase(query);
    }
}
