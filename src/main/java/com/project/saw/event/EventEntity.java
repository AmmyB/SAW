package com.project.saw.event;


import com.project.saw.ticket.TicketEntity;
import com.project.saw.user.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;
import org.springframework.hateoas.*;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "events")
public class EventEntity extends RepresentationModel<EventEntity>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String title;
    private String location;
    private Double price;
    @Column(name = "starting_date")
    private LocalDate startingDate;
    @Column(name = "ending_date")
    private LocalDate endingDate;
    private String description;

    @OneToOne(mappedBy = "eventEntity")
    private UserEntity userEntity;

    @OneToMany(mappedBy = "eventEntity")
    private Set<TicketEntity> ticketEntities;




}
