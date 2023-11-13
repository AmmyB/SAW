package com.project.saw.dto.user;


import com.project.saw.event.EventEntity;
import com.project.saw.ticket.TicketEntity;
import com.project.saw.user.UserRole;

import java.util.Set;

public interface UserProjections {

    Long getId();

    String getUserName();

    String getEmail();

    UserRole getUserRole();

    EventEntity getEventEntity();

    Set<TicketEntity> getTicketEntities();
}
