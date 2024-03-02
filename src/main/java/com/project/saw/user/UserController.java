package com.project.saw.user;

import com.project.saw.dto.user.CreateUserRequest;
import com.project.saw.dto.user.UserProjections;
import com.project.saw.exception.EmailExistsException;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@RestController
@RequestMapping("/user")
class UserController {

    private final UserService userService;

    @Autowired
    private  UserModelAssembler userModelAssembler;
    @Autowired
    private PagedResourcesAssembler<UserProjections> userProjectionsPagedResourcesAssembler;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Get User List", description = "Returns a list of all users" )
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<UserProjections>>> getUserList(Pageable pageable) {
        log.info("Fetching list of users");
        Page<UserProjections> userList =  userService.getUserList(pageable);
        Link link = linkTo(methodOn(UserController.class).getUserList(pageable)).withSelfRel();
        PagedModel<EntityModel<UserProjections>> pagedModel = userProjectionsPagedResourcesAssembler.toModel(userList,userModelAssembler);
        return ResponseEntity.ok(pagedModel);
    }

    @Operation(summary = "Create a new user", description = "All parameters are required. Method returns a new user object")
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody @Valid CreateUserRequest createUserRequest) throws EmailExistsException {
        User createUser = userService.createUser(createUserRequest);
        createUser.add(linkTo(UserController.class).slash(createUser.getId()).withSelfRel());
        log.info("Creating a new user: {}", createUserRequest);
        return ResponseEntity.ok(createUser);
    }

    @Operation(summary = "Delete an existing user and its associated data", description = "User id is required for deletion. " +
            "The method deletes the user and its associated data: removes the user's information from the Event " +
            "table and deletes all tickets associated with the user.")
    @DeleteMapping("{userId}")
    public ResponseEntity<Void> deleteUser( @PathVariable @Valid Long userId) {
        log.info("Deleting a user with the id: {}", userId);
        userService.delete(userId);
        return ResponseEntity.noContent().build();
    }
}
