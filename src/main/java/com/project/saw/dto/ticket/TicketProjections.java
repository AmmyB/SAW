package com.project.saw.dto.ticket;

import com.project.saw.event.Event;
import com.project.saw.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

public interface TicketProjections {

    Long getId();
    LocalDateTime getPurchaseDate();

    UUID getBarcode();

    Event getEventEntity();

    User getUserEntity();

}
