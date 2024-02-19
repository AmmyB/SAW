package com.project.saw.ticket;


import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@RestController
@RequestMapping("/ticket")
public class TicketController {

    private final TicketService ticketService;

    @Autowired
    private TicketModelAssembler ticketModelAssembler;

    @Autowired
    private PagedResourcesAssembler<Ticket> ticketPagedResourcesAssembler;

    public TicketController(TicketService ticketService){
        this.ticketService = ticketService;
    }

    @Operation(summary = "Get Ticket List for Event", description = "Returns a list of all tickets for given event")
    @GetMapping("/{eventId}/tickets")
    public ResponseEntity<CollectionModel<EntityModel<Ticket>>> getTicketListOfEvent(Pageable pageable, Long eventId){
        log.info("Fetching list of ticket for a given event");
        Page<Ticket> allTickets = ticketService.getTicketListforEvent(pageable,eventId);
        Link link = linkTo(methodOn(TicketController.class).getTicketListOfEvent(pageable,eventId)).withSelfRel();
        PagedModel<EntityModel<Ticket>> pagedModel = ticketPagedResourcesAssembler.toModel(allTickets,ticketModelAssembler);
        return ResponseEntity.ok(pagedModel);
    }


}
