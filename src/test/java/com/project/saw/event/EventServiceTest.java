package com.project.saw.event;

import com.project.saw.dto.CreateEventRequest;
import com.project.saw.dto.UpdateEventRequest;
import com.project.saw.dto.UpdateEventResponse;
import com.project.saw.exception.DuplicateException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;


class EventServiceTest {

    private EventRepository eventRepository;
    private EventService eventService;

    private static final EventEntity event = new EventEntity(4L, "Unsound Festival 2023: WEEKLY PASS", "Kraków", 760.00,
            LocalDate.of(2023,10,1),LocalDate.of(2023,10, 8),
            "W pierwszym tygodniu października, Unsound zaburzy więc stały, lokalny porządek miasta, organizując koncerty, całonocne imprezy klubowe, dyskusje i projekcje filmowe.",
            null, null);

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
        CreateEventRequest request = new CreateEventRequest("test", "test location", 9.00,
                LocalDate.of(2001,1,2), LocalDate.of(2001,1,2),
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

    @Test
    void given_repo_with_existing_event_when_add_new_event_then_event_should_not_be_created() {
        //given
        Mockito.when(eventRepository.findByTitle(any())).thenReturn(Optional.of(event));
        CreateEventRequest request = new CreateEventRequest("Unsound Festival 2023: WEEKLY PASS",
                "test location", 1.00, LocalDate.of(2001,1,2),
                LocalDate.of(2001,1,2), "Test description");
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
        Mockito.when(eventRepository.findById(any())).thenReturn(Optional.of(event));
        Mockito.when(eventRepository.save(any())).thenReturn(event);
        UpdateEventRequest request = new UpdateEventRequest(150.00,LocalDate.of(2001,1,2),
                LocalDate.of(2001,1,3),"Test description");
        UpdateEventResponse response = new UpdateEventResponse(event.getId(),event.getTitle(),event.getLocation(),
                request.getPrice(),request.getStartingDate(),request.getEndingDate(),request.getDescription());
        //when
        var results = eventService.updateEvent(4L,request);
        //then
        Mockito.verify(eventRepository,Mockito.times(1)).save(eventEntityArgumentCaptor.capture());
        var capturedParameter = eventEntityArgumentCaptor.getValue();
        Assertions.assertEquals(capturedParameter.getTitle(), response.getTitle());
        Assertions.assertEquals(capturedParameter.getLocation(), response.getLocation());
        Assertions.assertEquals(capturedParameter.getPrice(), response.getPrice());
        Assertions.assertEquals(capturedParameter.getStartingDate(), response.getStartingDate());
        Assertions.assertEquals(capturedParameter.getEndingDate(), response.getEndingDate());
        Assertions.assertEquals(capturedParameter.getDescription(), response.getDescription());
    }

    @Test
    void given_an_event_with_the_id_when_call_delete_method_with_the_id_then_event_should_be_deleted(){
        //given
        EventEntity eventEntity = new EventEntity(1L,"Test", "Kraków",5.00,LocalDate.of(2005,5,5),
                LocalDate.of(2005,5,6),"Test description", null, null);
        //when
        eventService.delete(eventEntity.getId());
        //then
        Mockito.verify(eventRepository, Mockito.times(1)).deleteById(eventEntity.getId());
        assertThat(eventRepository.findById(eventEntity.getId())).isEmpty();
    }
}