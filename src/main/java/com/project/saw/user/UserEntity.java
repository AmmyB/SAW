package com.project.saw.user;


import com.project.saw.event.EventEntity;
import com.project.saw.ticket.TicketEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
public class UserEntity extends RepresentationModel<UserEntity> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_name")
    @NotBlank(message = "Username is mandatory")
    private String userName;

    @NotBlank(message = "Password is mandatory")
    private String password;

    @Email(message = "Email should be valid")
    private String email;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @OneToOne
    private EventEntity eventEntity;

    @OneToMany(mappedBy = "userEntity")
    private Set<TicketEntity> ticketEntities;
}
