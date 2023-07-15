package com.project.saw.dto.event;


import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventResponse {

    private Long id;
    private String title;
    private String location;
    private Double price;
    private LocalDate startingDate;
    private LocalDate endingDate;
    private String description;
}
