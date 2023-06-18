package com.project.saw.event;

import com.project.saw.dto.CreateEventRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;


import static org.mockito.ArgumentMatchers.any;


class EventServiceTest {

    private EventRepository eventRepository;
    private EventService eventService;
    @Captor
    ArgumentCaptor<EventEntity> eventEntityArgumentCaptor;

    @BeforeEach
    void setUp() {
        eventEntityArgumentCaptor = ArgumentCaptor.forClass(EventEntity.class);
        eventRepository = Mockito.mock(EventRepository.class);
        eventService = new EventService(eventRepository);
    }

    @Test
    void given_not_empty_list_when_fetch_the_list_then_events_list_should_be_returned() {
        //given
        List<EventEntity> list = Arrays.asList(new EventEntity(), new EventEntity());
        Mockito.when(eventRepository.findAll()).thenReturn(list);
        //when
        var results = eventService.getAll();
        //then
        Assertions.assertEquals(list, results);

    }

    @Test
    void given_repo_with_not_existing_event_when_add_new_event_then_event_should_be_created() {
        //given
        CreateEventRequest request = new CreateEventRequest("test", "testLocation", 9.00, LocalDate.of(2001,1,2), LocalDate.of(2001,1,2),
                "Test description");
        //when
        var results = eventService.createEvent(request);
        //then
        Mockito.verify(eventRepository, Mockito.times(1)).save(eventEntityArgumentCaptor.capture());
        var capturedParameter = eventEntityArgumentCaptor.getValue();
        Assertions.assertEquals(capturedParameter.getTitle(),request.getTitle());
        Assertions.assertEquals(capturedParameter.getLocation(),request.getLocation());
        Assertions.assertEquals(capturedParameter.getPrice(),request.getPrice());
        Assertions.assertEquals(capturedParameter.getStartingDate(),request.getStartingDate());
        Assertions.assertEquals(capturedParameter.getEndingDate(),request.getEndingDate());
        Assertions.assertEquals(capturedParameter.getDescription(),request.getDescription());
    }
}