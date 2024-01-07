package com.project.saw.user;

import com.project.saw.dto.user.CreateUserRequest;
import com.project.saw.dto.user.UserProjections;
import com.project.saw.exception.EmailExistsException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserProjections> getUserList() {
        return userService.getUserList();
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody @Valid CreateUserRequest createUserRequest) throws EmailExistsException {
        User createUser = userService.createUser(createUserRequest);
        createUser.add(linkTo(UserController.class).slash(createUser.getId()).withSelfRel());
        log.info("Creating a new user: {}", createUserRequest);
        return ResponseEntity.ok(createUser);
    }

    @DeleteMapping("{userId}")
    public void deleteUser( @PathVariable @Valid Long userId) {
        log.info("Deleting a user with the id: {}", userId);
        userService.delete(userId);
    }
}
