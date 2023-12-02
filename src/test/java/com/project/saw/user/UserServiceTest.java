package com.project.saw.user;

import com.project.saw.dto.user.CreateUserRequest;
import com.project.saw.dto.user.UserProjections;
import com.project.saw.exception.EmailExistsException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.data.domain.Sort;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;


class UserServiceTest {


    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserService userService;

    @Captor
    ArgumentCaptor<UserEntity> userEntityArgumentCaptor;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        userService = new UserService(userRepository, passwordEncoder);
        userEntityArgumentCaptor = ArgumentCaptor.forClass(UserEntity.class);

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
        Assertions.assertEquals(Collections.singletonList(userProjections), results);
    }

    @Test
    void given_repo_with_not_existing_user_when_add_new_user_then_user_should_be_created() throws EmailExistsException {
        //given
        CreateUserRequest request = new CreateUserRequest("unitTest", "xxx4", "xyz@nq.pl");
        UserEntity response = UserEntity.builder()
                .userName(request.userName()).build();
        Mockito.when(userRepository.save(any())).thenReturn(response);
        //when
        var result = userService.createUser(request);
        //then
        Assertions.assertEquals(result.getUserName(), request.userName());


    }


}