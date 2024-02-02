package com.project.saw.user;


import com.project.saw.event.Event;
import com.project.saw.ticket.Ticket;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.util.Set;


@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Users")
public class User extends RepresentationModel<User> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "user_name", unique = true)
    @NotBlank(message = "Username is mandatory")
    private String userName;

    @NotBlank(message = "Password is mandatory")
    private String password;

    @Column(unique = true)
    @Email(message = "Email should be valid")
    private String email;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @OneToOne
    private Event eventEntity;

    @OneToMany(mappedBy = "userEntity")
    private Set<Ticket> ticketEntities;
}
