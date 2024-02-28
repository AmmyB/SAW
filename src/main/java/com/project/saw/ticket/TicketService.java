package com.project.saw.ticket;


import com.project.saw.dto.ticket.TicketProjections;
import com.project.saw.event.Event;
import com.project.saw.event.EventRepository;
import com.project.saw.exception.ExceptionMessage;
import com.project.saw.user.User;
import com.project.saw.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public Page<Ticket> getTicketListforEvent(Pageable pageable, Long eventId) {
        return ticketRepository.sortedListOfTicketForEvent(pageable, eventId);
    }


    public TicketProjections findTicket(Long ticketId) {
       return ticketRepository.findTicketById(ticketId);
    }

    public Ticket createTicket(Long eventId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository.findByUserNameIgnoreCase(username)
                .orElseThrow(()-> new UsernameNotFoundException(username));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.EVEN_NOT_FOUND_ERROR_MESSAGE + eventId));

        Ticket ticket = Ticket.builder()
                .userEntity(user)
                .eventEntity(event)
                .purchaseDate(LocalDateTime.now())
                .build();
        return ticketRepository.save(ticket);
    }
}
