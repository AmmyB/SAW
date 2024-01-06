package com.project.saw.dto.event;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventRequest {

    //@NotNull(message = "Price is mandatory")
    private Double price;
    //@FutureOrPresent(message = "Starting date should be future or present")
    private LocalDate startingDate;
    //@Future(message = "Ending date should be future")
    private LocalDate endingDate;

    //@NotBlank(message = "Description is mandatory")
    private String description;


}
