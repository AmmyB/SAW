package com.project.saw.dto.event;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventResponse extends RepresentationModel<UpdateEventResponse> {

    private Long id;
    private String title;
    private String location;
    private Double price;
    private LocalDate startingDate;
    private LocalDate endingDate;
    private Integer seatingCapacity;
    private String description;
}
