package com.project.saw.user;

import com.project.saw.dto.user.CreateUserRequest;
import com.project.saw.dto.user.UserProjections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public UserEntity createUser(@RequestBody CreateUserRequest createUserRequest) {
        log.info("Creating a new user: {}", createUserRequest);
        return userService.createUser(createUserRequest);
    }
}
