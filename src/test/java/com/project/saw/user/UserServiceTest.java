package com.project.saw.user;

import com.project.saw.dto.user.UserProjections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Sort;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.util.Collections;


class UserServiceTest {


    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserService userService;


    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        userService = new UserService(userRepository, passwordEncoder);

    }

    ProjectionFactory factory = new SpelAwareProxyProjectionFactory();
    UserProjections userProjections = factory.createProjection(UserProjections.class);


    @Test
    void given_not_empty_list_when_fetch_the_list_then_users_list_should_be_returned() {
        //given
        userProjections.setId(4L);
        userProjections.setUserName("testUser");
        userProjections.setEmail("xyz@vp.pl");
        userProjections.setUserRole(UserRole.PARTICIPANT);
        userProjections.setEventEntity(null);
        userProjections.setTicketEntity(null);

        Mockito.when(userRepository.listOfUsers(Sort.by("id"))).thenReturn(Collections.singletonList(userProjections));

//        when
        var results = userService.getUserList();

//        then
        Assertions.assertEquals(Collections.singletonList(userProjections),results);
    }


}