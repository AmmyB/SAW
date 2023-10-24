package com.project.saw.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserService userService;
    private UserRepository userRepository;

    @BeforeEach
    void setUp(){
        userService = Mockito.mock(UserService.class);
        userRepository = Mockito.mock(UserRepository.class);
    }




}