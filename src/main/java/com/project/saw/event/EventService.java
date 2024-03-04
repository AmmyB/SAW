package com.project.saw.event;


import com.project.saw.dto.event.CreateEventRequest;
import com.project.saw.dto.event.UpdateEvenMapper;
import com.project.saw.dto.event.UpdateEventRequest;
import com.project.saw.dto.event.UpdateEventResponse;
import com.project.saw.exception.DuplicateException;
import com.project.saw.exception.ExceptionMessage;
import com.project.saw.ticket.Ticket;
import com.project.saw.ticket.TicketRepository;
import com.project.saw.user.User;
import com.project.saw.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
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

    private final TicketRepository ticketRepository;

    private final UserRepository userRepository;


    public EventService(EventRepository eventRepository, UpdateEvenMapper updateEvenMapper, TicketRepository ticketRepository, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.updateEvenMapper = updateEvenMapper;
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
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

    @Transactional
    public void delete(Long eventId) {
        Event eventToDelete = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.EVEN_NOT_FOUND_ERROR_MESSAGE + eventId));

        User userEntity = eventToDelete.getUserEntity();
        if (userEntity != null) {
            userEntity.setEventEntity(null);
            userRepository.save(userEntity);
        }

        for (Ticket ticket : eventToDelete.getTicketEntities()) {
            ticket.setUserEntity(null);
            ticket.setEventEntity(null);
            ticketRepository.save(ticket);
        }
        ticketRepository.deleteAll(eventToDelete.getTicketEntities());

        eventRepository.delete(eventToDelete);
    }


    public List<Event> searchEvents(String query) {
        return eventRepository.searchByTitleLikeIgnoreCase(query);
    }
}
