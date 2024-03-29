package com.project.saw.ticket;

import com.project.saw.dto.ticket.TicketProjections;
import com.project.saw.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @Query("SELECT t FROM Ticket t WHERE t.eventEntity.id = :eventId")
    Page<Ticket> sortedListOfTicketForEvent(Pageable pageable, Long eventId);

    @Query("SELECT t FROM Ticket t WHERE t.id = :ticketId")
    TicketProjections findTicketById(Long ticketId);

}
