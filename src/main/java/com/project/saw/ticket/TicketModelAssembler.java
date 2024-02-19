package com.project.saw.ticket;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class TicketModelAssembler implements RepresentationModelAssembler<Ticket, EntityModel<Ticket>> {

    @Override
    public EntityModel<Ticket> toModel(Ticket ticket) {
        return EntityModel.of(ticket, linkTo(methodOn(TicketController.class).getTicketListOfEvent(null, ticket.getEventEntity().getId())).withSelfRel());
    }
}
