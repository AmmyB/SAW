package com.project.saw.ticket;

import com.project.saw.dto.ticket.TicketProjections;
import com.project.saw.event.Event;
import com.project.saw.event.EventRepository;
import com.project.saw.user.User;
import com.project.saw.user.UserRepository;
import com.project.saw.user.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;

class TicketServiceTest {

    private TicketRepository ticketRepository;
    private TicketService ticketService;
    private EventRepository eventRepository;
    private UserRepository userRepository;
    private Authentication authentication;

    public static final User USER = new User(7L, "staticUser",
            "Yyy47%", "yxq14@tr.pl", UserRole.PARTICIPANT, null, null);

    public static final Event EVENT = new Event(1L, "Test", "Kraków", 5.00, LocalDate.of(2005, 5, 5),
            LocalDate.of(2005, 5, 6), 30, "Test description", null, null);

    public static final Ticket TICKET = new Ticket(2L, LocalDateTime.now(), UUID.randomUUID(), EVENT, USER);

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        ticketRepository = Mockito.mock(TicketRepository.class);
        eventRepository = Mockito.mock(EventRepository.class);
        authentication = Mockito.mock(Authentication.class);
        ticketService = new TicketService(ticketRepository, eventRepository, userRepository);

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void given_eventId_when_call_getTicketListforEvent_then_tickets_list_should_be_returned() {
        // given
        List<Ticket> tickets = new ArrayList<>();
        tickets.add(TICKET);
        Page<Ticket> page = new PageImpl<>(tickets, PageRequest.of(0, 10), tickets.size());
        Mockito.when(ticketRepository.sortedListOfTicketForEvent(any(Pageable.class), anyLong())).thenReturn(page);
        Pageable pageable = PageRequest.of(0, 10);
        // when
        Page<Ticket> result = ticketService.getTicketListforEvent(pageable, EVENT.getId());
        // then
        Assertions.assertEquals(page, result);
        Mockito.verify(ticketRepository, Mockito.times(1)).sortedListOfTicketForEvent(pageable, EVENT.getId());
    }

    @Test
    void given_ticketId_when_call_findTicket_method_then_ticket_projection_should_be_returned() {
        // given
        TicketProjections ticketProjections = Mockito.mock(TicketProjections.class);
        Mockito.when(ticketRepository.findTicketById(anyLong())).thenReturn(ticketProjections);
        // when
        TicketProjections result = ticketService.findTicket(TICKET.getId());
        // then
        assertEquals(ticketProjections, result);
    }

    @Test
    void given_eventId_when_call_createTicket_method_then_new_ticket_should_be_created() {
        // given
        Mockito.when(authentication.getName()).thenReturn("username");
        Mockito.when(userRepository.findByUserNameIgnoreCase("username")).thenReturn(Optional.of(USER));
        Mockito.when(eventRepository.findById(EVENT.getId())).thenReturn(Optional.of(EVENT));
        Mockito.when(ticketRepository.save(any())).thenReturn(TICKET);
        // when
        Ticket result = ticketService.createTicket(EVENT.getId());
        // then
        assertEquals(TICKET, result);
    }

    @Test
    void given_ticketId_when_call_deleteTicket_method_then_ticket_and_all_related_info_should_be_deleted() {
        // given
        Ticket ticket = new Ticket(2L, LocalDateTime.now(), UUID.randomUUID(), EVENT, USER);
        Mockito.when(ticketRepository.findById(ticket.getId())).thenReturn(Optional.of(ticket));

        // when
        ticketService.deleteTicket(ticket.getId());

        // then
        Mockito.verify(ticketRepository, Mockito.times(1)).findById(ticket.getId());
        Mockito.verify(ticketRepository, Mockito.times(1)).delete(ticket);
        Mockito.verify(eventRepository, Mockito.never()).save(Mockito.any());
    }
}