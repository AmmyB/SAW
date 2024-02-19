package com.project.saw.ticket;


import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TicketService{

    private final TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository){
        this.ticketRepository = ticketRepository;
    }

    public Page<Ticket> getTicketListforEvent(Pageable pageable, Long eventId){
        return ticketRepository.sortedListOfTicketForEvent(pageable, eventId);
    }
}
