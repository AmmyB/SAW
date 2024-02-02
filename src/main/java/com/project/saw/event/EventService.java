package com.project.saw.event;


import com.project.saw.dto.event.CreateEventRequest;
import com.project.saw.dto.event.UpdateEvenMapper;
import com.project.saw.dto.event.UpdateEventRequest;
import com.project.saw.dto.event.UpdateEventResponse;
import com.project.saw.exception.DuplicateException;
import com.project.saw.exception.ExceptionMessage;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;




@Slf4j
@Service
public class EventService {

    private final EventRepository eventRepository;
    private final UpdateEvenMapper updateEvenMapper;


    public EventService(EventRepository eventRepository, UpdateEvenMapper updateEvenMapper) {
        this.eventRepository = eventRepository;
        this.updateEvenMapper = updateEvenMapper;
    }


    public Page<Event> getEventList(Pageable pageable) {
        return eventRepository.sortedListOfEvents(pageable);

    }

    public Event createEvent(CreateEventRequest request) {
        eventRepository.findByTitleIgnoreCase(request.getTitle()).stream().findAny()
                .ifPresent(EventEntity -> {
                    var error = String.format(ExceptionMessage.DUPLICATE_EVENT_ERROR_MESSAGE, request.getTitle());
                    throw new DuplicateException(error);
                });
        Event eventEntity = Event.builder()
                .title(request.getTitle())
                .location(request.getLocation())
                .price(request.getPrice())
                .startingDate(request.getStartingDate())
                .endingDate(request.getEndingDate())
                .description(request.getDescription())
                .build();
            eventRepository.save(eventEntity);
        return eventEntity;
    }

    public UpdateEventResponse updateEvent(Long eventId, UpdateEventRequest updateEventRequest) {
        Event eventEntity = eventRepository.findById(eventId)
                .map(event -> setUpdateEvent(event, updateEventRequest))
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.EVEN_NOT_FOUND_ERROR_MESSAGE + eventId));

        eventRepository.save(eventEntity);
        return eventToEventResponse(eventEntity);
    }

    private Event setUpdateEvent(Event event, UpdateEventRequest request) {
        return updateEvenMapper.ToEntity(event, request);

    }

    private UpdateEventResponse eventToEventResponse(Event eventEntity) {
        return new UpdateEventResponse(
                eventEntity.getId(),
                eventEntity.getTitle(),
                eventEntity.getLocation(),
                eventEntity.getPrice(),
                eventEntity.getStartingDate(),
                eventEntity.getEndingDate(),
                eventEntity.getSeatingCapacity(),
                eventEntity.getDescription()
        );

    }

    public void delete(Long eventId) {
        Event eventToDelete = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.EVEN_NOT_FOUND_ERROR_MESSAGE + eventId));
        eventRepository.delete(eventToDelete);

    }

    public List<Event> searchEvents(String query) {
        return eventRepository.searchByTitleLikeIgnoreCase(query);
    }
}
