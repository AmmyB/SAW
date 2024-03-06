package com.project.saw.ticket;

import com.project.saw.dto.ticket.TicketProjections;
import com.project.saw.event.Event;
import com.project.saw.event.EventRepository;
import com.project.saw.exception.NoAvailableSeatsException;
import com.project.saw.user.User;
import com.project.saw.user.UserRepository;
import com.project.saw.user.UserRole;
import jakarta.persistence.EntityNotFoundException;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;

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
    void given_eventId_when_call_createTicket_method_then_new_ticket_should_be_created() throws NoAvailableSeatsException {
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
    void given_invalidEventId_when_call_createTicket_then_method_throws_EntityNotFoundException_and_should_not_create_a_ticket() {
        // given
        Long invalidEventId = 100L;
        User user = new User();
        user.setUserName("username");
        Mockito.when(authentication.getName()).thenReturn(user.getUserName());
        Mockito.when(userRepository.findByUserNameIgnoreCase(user.getUserName())).thenReturn(Optional.of(user));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Mockito.when(eventRepository.findById(invalidEventId)).thenReturn(Optional.empty());
        // when
        var exception = assertThrows(EntityNotFoundException.class, () -> ticketService.createTicket(invalidEventId));
        //then
        Mockito.verify(ticketRepository, Mockito.never()).save(any());
        var expectedMessage = String.format("Event not found with id: " + invalidEventId);
        var actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void given_noAvailableSeats_when_call_createTicket_then_method_throws_NoAvailableSeatsException_and_should_not_create_a_ticket() {
        // given
        Long eventId = 1L;
        Event event = new Event();
        event.setTitle("title test");
        event.setSeatingCapacity(0);
        User user = new User();
        user.setUserName("username");
        Mockito.when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        Mockito.when(authentication.getName()).thenReturn(user.getUserName());
        Mockito.when(userRepository.findByUserNameIgnoreCase(user.getUserName())).thenReturn(Optional.of(user));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // when
        var exception = assertThrows(NoAvailableSeatsException.class, () -> ticketService.createTicket(eventId));
        //then
        Mockito.verify(ticketRepository, Mockito.never()).save(any());
        var expectedMessage = String.format("No available seats for this event: " + event.getTitle());
        var actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void given_non_existent_user_when_call_createTicket_then_method_throws_UsernameNotFoundException_and_should_not_create_a_ticket() {
        // given
        Long eventId = 1L;
        Event event = new Event();
        event.setSeatingCapacity(1);
        String username = "username";
        Mockito.when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        Mockito.when(authentication.getName()).thenReturn(username);
        Mockito.when(userRepository.findByUserNameIgnoreCase(username)).thenReturn(Optional.empty());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // when
        var exception = assertThrows(UsernameNotFoundException.class, () -> ticketService.createTicket(eventId));
        //then
        Mockito.verify(ticketRepository, Mockito.never()).save(any());
        var expectedMessage = String.format("User not found with username: " + username);
        var actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
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