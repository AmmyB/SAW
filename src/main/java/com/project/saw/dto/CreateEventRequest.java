package com.project.saw.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateEventRequest {

        private String title;
        private String location;
        private Double price;
        private LocalDate startingDate;
        private LocalDate endingDate;
        private String description;


}
