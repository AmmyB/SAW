package com.project.saw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventRequest {

    private Double price;
    private LocalDate startingDate;
    private LocalDate endingDate;
    private String description;


}
