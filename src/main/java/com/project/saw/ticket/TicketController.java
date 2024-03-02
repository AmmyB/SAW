package com.project.saw.ticket;


import com.project.saw.dto.ticket.TicketProjections;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.*;


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

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @Operation(summary = "Get Ticket List for Event", description = "Returns a list of all tickets for given event")
    @GetMapping("/{eventId}/tickets")
    public ResponseEntity<CollectionModel<EntityModel<Ticket>>> getTicketListOfEvent(Pageable pageable, @PathVariable @Valid Long eventId) {
        log.info("Fetching list of ticket for a given event");
        Page<Ticket> allTickets = ticketService.getTicketListforEvent(pageable, eventId);
        Link link = linkTo(methodOn(TicketController.class).getTicketListOfEvent(pageable, eventId)).withSelfRel();
        PagedModel<EntityModel<Ticket>> pagedModel = ticketPagedResourcesAssembler.toModel(allTickets, ticketModelAssembler);
        return ResponseEntity.ok(pagedModel);
    }

    @Operation(summary = "Get a Ticket by Id", description = "Returns a Tickets for given ID")
    @GetMapping("/{ticketId}")
    public ResponseEntity<TicketProjections> getTicketDetails(@PathVariable @Valid Long ticketId) {
        log.info("Fetching a ticket: {}", ticketId);
        TicketProjections ticket = ticketService.findTicket(ticketId);
        Link link = linkTo(methodOn(TicketController.class).getTicketDetails(ticketId)).withSelfRel();
        return ResponseEntity.ok(ticket);
    }

    @Operation(summary = "Create a new ticket", description = "All parameters are required. Method returns a new ticket object for the given user and event")
    @PostMapping("{eventId}")
    public ResponseEntity<Ticket> createTicket(@PathVariable @Valid Long eventId){
        log.info("Creating a ticket for an event: {}", eventId);
        Ticket createTicket = ticketService.createTicket(eventId);
        createTicket.add(linkTo(methodOn(TicketController.class).createTicket(eventId)).withSelfRel());
        return ResponseEntity.ok(createTicket);
    }

    @Operation(summary = "Delete an existing ticket and its associated data.", description = "Ticket id is required for deletion. " +
            "The method removes the ticket and its associated data: " +
            "deletes the ticket information in the Users and Events tables.")
    @DeleteMapping("{ticketId}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long ticketId){
        log.info("Deleting a ticket with the id: {}", ticketId);
        ticketService.deleteTicket(ticketId);
        return ResponseEntity.noContent().build();
    }
}

