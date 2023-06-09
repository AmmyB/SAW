package com.project.saw.user;


import com.project.saw.event.EventEntity;
import com.project.saw.ticket.TicketEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;


@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_name")
    private String userName;
    private String password;
    private String email;

    @Enumerated(EnumType.STRING)
    private UserRole userRole = UserRole.PARTICIPANT;

    @OneToOne
    private EventEntity eventEntity;

    @OneToMany(mappedBy = "userEntity")
    private Set<TicketEntity> ticketEntities;
}
