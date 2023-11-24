package com.project.saw.dto.user;


import com.project.saw.event.EventEntity;
import com.project.saw.ticket.TicketEntity;
import com.project.saw.user.UserRole;
import lombok.RequiredArgsConstructor;

import java.util.Set;

public interface UserProjections {

    Long getId();

    String getUserName();

    String getEmail();

    UserRole getUserRole();

    EventEntity getEventEntity();

    Set<TicketEntity> getTicketEntities();

    void setId(Long id);

    void setUserName(String userName);

    void setEmail(String email);

    void setUserRole(UserRole userRole);

    void setEventEntity(EventEntity eventEntity);

    void setTicketEntity(Set<TicketEntity> ticketEntities);

}
