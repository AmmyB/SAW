package com.project.saw.ticket;


import com.project.saw.event.Event;
import com.project.saw.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Builder
@AllArgsConstructor
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "purchase_date")
    @CreationTimestamp
    private LocalDateTime purchaseDate;

    private UUID barcode;

    @ManyToOne
    private Event eventEntity;

    @ManyToOne
    private User userEntity;

    public Ticket(){
        this.barcode = UUID.randomUUID();
    }
}

