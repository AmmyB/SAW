package com.project.saw.ticket;


import com.project.saw.dto.ticket.TicketProjections;
import com.project.saw.event.Event;
import com.project.saw.event.EventRepository;
import com.project.saw.exception.ExceptionMessage;
import com.project.saw.exception.NoAvailableSeatsException;
import com.project.saw.user.User;
import com.project.saw.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;


@Slf4j
@Service
public class TicketService {

    private final TicketRepository ticketRepository;

    private EventRepository eventRepository;

    private UserRepository userRepository;

    public TicketService(TicketRepository ticketRepository, EventRepository eventRepository, UserRepository userRepository) {
        this.ticketRepository = ticketRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    public Page<Ticket> getTicketListforEvent(Pageable pageable, Long eventId) {
        return ticketRepository.sortedListOfTicketForEvent(pageable, eventId);
    }


    public TicketProjections findTicket(Long ticketId) {
       return ticketRepository.findTicketById(ticketId);
    }

    public Ticket createTicket(Long eventId) throws NoAvailableSeatsException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository.findByUserNameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.EVEN_NOT_FOUND_ERROR_MESSAGE + eventId));

        if (event.getSeatingCapacity() > 0) {
            event.setSeatingCapacity(event.getSeatingCapacity() - 1);
            eventRepository.save(event);

            Ticket ticket = Ticket.builder()
                    .userEntity(user)
                    .eventEntity(event)
                    .purchaseDate(LocalDateTime.now())
                    .build();
            return ticketRepository.save(ticket);
        } else {
            throw new NoAvailableSeatsException(ExceptionMessage.SEATS_NO_AVAILABLE_EXCEPTION_MESSAGE);
        }
    }

    @Transactional
    public void deleteTicket(Long ticketId){
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.TICKET_NOT_FOUND_EXCEPTION_MESSAGE + ticketId));

        User user = ticket.getUserEntity();
        if (user.getTicketEntities() != null) {
            user.getTicketEntities().remove(ticket);
        }

        Event event = ticket.getEventEntity();
        if (event != null && event.getTicketEntities() != null) {
            event.getTicketEntities().remove(ticket);
        }

        ticketRepository.delete(ticket);
    }

}
