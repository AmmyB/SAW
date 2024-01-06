package com.project.saw.dto.event;
import com.project.saw.event.Event;
import org.mapstruct.Mapper;

import org.mapstruct.MappingTarget;



@Mapper(componentModel = "spring")
public interface UpdateEvenMapper {

    void ToEntity(@MappingTarget Event eventEntity, UpdateEventRequest request );

}
