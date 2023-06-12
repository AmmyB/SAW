package com.project.saw.event;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EventServiceTest {

    private EventRepository eventRepository;
    private EventService eventService;

    @BeforeEach
    void setUp() {
        eventRepository = Mockito.mock(EventRepository.class);
        eventService = new EventService(eventRepository);
    }

    @Test
    public void test_getAll_not_with_empty_list() {
        //given
        List<EventEntity> list = Arrays.asList(new EventEntity(),new EventEntity());
        Mockito.when(eventRepository.findAll()).thenReturn(list);
        //when
        var results = eventService.getAll();
        //then
        Assertions.assertEquals(list,results);

    }

    @Test
    public void test_CreateEvent() {


    }
}