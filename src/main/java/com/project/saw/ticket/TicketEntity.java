package com.project.saw.ticket;


import com.project.saw.event.EventEntity;
import com.project.saw.user.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tickets")
public class TicketEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "purchase_date")
    @CreationTimestamp
    private LocalDateTime purchaseDate;

    @ManyToOne
    private EventEntity eventEntity;

    @ManyToOne
    private UserEntity userEntity;

}
