package com.project.saw.event;


import com.project.saw.ticket.TicketEntity;
import com.project.saw.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
@Table(name = "Events")
public class Event extends RepresentationModel<Event>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @NotBlank(message = "Title is mandatory")
    private String title;
    @NotBlank(message = "Location is mandatory")
    private String location;
    @NotNull(message = "Price is mandatory")
    private Double price;
    @Column(name = "starting_date")
    @FutureOrPresent(message = "Starting date should be future or present")
    private LocalDate startingDate;
    @Column(name = "ending_date")
    @Future(message = "Ending date should be future")
    private LocalDate endingDate;
    @NotBlank(message = "Description is mandatory")
    private String description;

    @OneToOne(mappedBy = "eventEntity")
    private User userEntity;

    @OneToMany(mappedBy = "eventEntity")
    private Set<TicketEntity> ticketEntities;




}
