package com.project.saw.event;

import com.project.saw.dto.event.CreateEventRequest;
import com.project.saw.dto.event.UpdateEvenMapper;
import com.project.saw.dto.event.UpdateEventRequest;
import com.project.saw.dto.event.UpdateEventResponse;
import com.project.saw.exception.DuplicateException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;


import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;


class EventServiceTest {

    private EventRepository eventRepository;
    private EventService eventService;
    private UpdateEvenMapper updateEvenMapper;

    private static final Event EVENT = new Event(4L, "Unsound Festival 2023: WEEKLY PASS", "Kraków", 760.00,
            LocalDate.of(2023, 10, 1), LocalDate.of(2023, 10, 8),
            "W pierwszym tygodniu października, Unsound zaburzy więc stały, lokalny porządek miasta, organizując koncerty, całonocne imprezy klubowe, dyskusje i projekcje filmowe.",
            null, null);


    @Captor
    ArgumentCaptor<Event> eventEntityArgumentCaptor;

    @BeforeEach
    void setUp() {
        eventEntityArgumentCaptor = ArgumentCaptor.forClass(Event.class);
        eventRepository = Mockito.mock(EventRepository.class);
        updateEvenMapper = Mockito.mock(UpdateEvenMapper.class);
        eventService = new EventService(eventRepository, updateEvenMapper);
    }

    @Test
    void given_not_empty_list_when_fetch_the_list_then_event_list_should_be_returned() {
        //given
        List<Event> list = Stream.of(EVENT, new Event(5L, "testowy event", "Warszawa", 10.00,
                        LocalDate.of(2023, 9, 24), LocalDate.of(2023, 9, 24),
                        "test test",
                        null, null))
                .sorted(Comparator.comparing(Event::getStartingDate))
                .filter(e -> e.getEndingDate()
                        .isAfter(LocalDate.now()))
                .toList();
        Mockito.when(eventRepository.findAll()).thenReturn(list);
        //when
        //var results = eventService.getEventList();
        //then
        //Assertions.assertEquals(list, results);

    }

    @Test
    void given_repo_with_not_existing_event_when_add_new_event_then_event_should_be_created() {
        //given
        CreateEventRequest request = new CreateEventRequest("test", "test location", 9.00,
                LocalDate.of(2001, 1, 2), LocalDate.of(2001, 1, 2),
                "Test description");
        //when
        var results = eventService.createEvent(request);
        //then
        Mockito.verify(eventRepository, Mockito.times(1)).save(eventEntityArgumentCaptor.capture());
        var capturedParameter = eventEntityArgumentCaptor.getValue();
        Assertions.assertEquals(capturedParameter.getTitle(), request.getTitle());
        Assertions.assertEquals(capturedParameter.getLocation(), request.getLocation());
        Assertions.assertEquals(capturedParameter.getPrice(), request.getPrice());
        Assertions.assertEquals(capturedParameter.getStartingDate(), request.getStartingDate());
        Assertions.assertEquals(capturedParameter.getEndingDate(), request.getEndingDate());
        Assertions.assertEquals(capturedParameter.getDescription(), request.getDescription());
    }

    @Test
    void given_repo_with_existing_event_when_add_new_event_then_event_should_not_be_created() {
        //given
        Mockito.when(eventRepository.findByTitleIgnoreCase(any())).thenReturn(Optional.of(EVENT));
        CreateEventRequest request = new CreateEventRequest("Unsound Festival 2023: WEEKLY PASS",
                "test location", 1.00, LocalDate.of(2001, 1, 2),
                LocalDate.of(2001, 1, 2), "Test description");
        //when
        var exception = Assertions.assertThrows(DuplicateException.class, () -> eventService.createEvent(request));
        //then
        Mockito.verify(eventRepository, Mockito.never()).save(any());
        var expectedMessage = String.format("This event %s already exist", request.getTitle());
        var actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));

    }

    @Test
    void given_existing_event_when_add_other_details_about_the_event_with_given_id_then_event_should_be_updated() {
        //given
        UpdateEventRequest request = new UpdateEventRequest(150.00, LocalDate.of(2001, 1, 2),
                LocalDate.of(2001, 1, 3), "Test description");
        UpdateEventResponse response = new UpdateEventResponse(EVENT.getId(), EVENT.getTitle(), EVENT.getLocation(),
                request.getPrice(), request.getStartingDate(), request.getEndingDate(), request.getDescription());
        Event event = new Event(EVENT.getId(), EVENT.getTitle(), EVENT.getLocation(),
                request.getPrice(), request.getStartingDate(), request.getEndingDate(), request.getDescription(),null,null);
        Mockito.when(eventRepository.findById(any())).thenReturn(Optional.of(EVENT));
        Mockito.when(eventRepository.save(any())).thenReturn(event);
        Mockito.when(updateEvenMapper.ToEntity(EVENT,request)).thenReturn(event);
        //when
        var results = eventService.updateEvent(4L, request);
        //then
        Mockito.verify(eventRepository, Mockito.times(1)).save(eventEntityArgumentCaptor.capture());
        var capturedParameter = eventEntityArgumentCaptor.getValue();
        Assertions.assertEquals(capturedParameter.getTitle(), response.getTitle());
        Assertions.assertEquals(capturedParameter.getLocation(), response.getLocation());
        Assertions.assertEquals(capturedParameter.getPrice(), response.getPrice());
        Assertions.assertEquals(capturedParameter.getStartingDate(), response.getStartingDate());
        Assertions.assertEquals(capturedParameter.getEndingDate(), response.getEndingDate());

    }

    @Test
    void given_not_existing_event_when_add_other_details_about_the_event_with_given_id_then_method_should_throws_exception() {
        //given
        Long eventId = 4L;
        UpdateEventRequest request = new UpdateEventRequest(150.00, LocalDate.of(2001, 1, 2),
                LocalDate.of(2001, 1, 3), "Test description");
        //when
        var exception = Assertions.assertThrows(EntityNotFoundException.class, ()-> eventService.updateEvent(eventId,request));
        //then
        Mockito.verify(eventRepository, Mockito.never()).save(any());
        var expectedMessage =  String.format("Event not found with id: " +  eventId);
        var actualMessage = exception.getMessage();
        Assertions.assertEquals(expectedMessage,actualMessage);
    }

    @Test
    void given_existing_event_when_call_delete_method_with_event_id_then_event_should_be_deleted() {
        //given
        Event event = new Event(1L, "Test", "Kraków", 5.00, LocalDate.of(2005, 5, 5),
                LocalDate.of(2005, 5, 6), "Test description", null, null);
        Mockito.when(eventRepository.findById(any())).thenReturn(Optional.of(event)).thenReturn(Optional.empty());
        //when
        eventService.delete(event.getId());
        //then
        Mockito.verify(eventRepository, Mockito.times(1)).delete(event);
        assertThat(eventRepository.findById(event.getId()).isEmpty());
    }

    @Test
    void given_not_existing_event_when_call_delete_method_with_not_existing_event_id_then_method_throws_exception() {
        //given
        Event event = new Event(1L, "Test", "Kraków", 5.00, LocalDate.of(2005, 5, 5),
                LocalDate.of(2005, 5, 6), "Test description", null, null);
        //when
        var exception = Assertions.assertThrows(EntityNotFoundException.class, ()->eventService.delete(event.getId()));
        //then
        Mockito.verify(eventRepository, Mockito.never()).delete(any());
        var expectedMessage =  String.format("Event not found with id: " +  event.getId());
        var actualMessage = exception.getMessage();
        Assertions.assertEquals(expectedMessage,actualMessage);
    }

    @Test
    void given_a_query_about_event_title_when_call_search_method_with_the_query_then_search_method_in_repository_should_be_called() {
        //given
        String q = "te";
        //when
        var results = eventService.searchEvents(q);
        //then
        Mockito.verify(eventRepository, Mockito.times(1)).searchByTitleLikeIgnoreCase(q);
    }
}