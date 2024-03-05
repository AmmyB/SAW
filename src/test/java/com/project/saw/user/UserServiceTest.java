package com.project.saw.user;

import com.project.saw.dto.user.CreateUserRequest;
import com.project.saw.dto.user.UserProjections;
import com.project.saw.event.Event;
import com.project.saw.event.EventRepository;
import com.project.saw.exception.DuplicateException;
import com.project.saw.exception.EmailExistsException;
import com.project.saw.ticket.Ticket;
import com.project.saw.ticket.TicketRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.data.domain.*;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;


class UserServiceTest {


    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserService userService;
    private TicketRepository ticketRepository;
    private EventRepository eventRepository;

    public static final User USER = new User(7L, "staticUser",
            "Yyy47%", "yxq14@tr.pl", UserRole.PARTICIPANT, null, null);

    @Captor
    ArgumentCaptor<User> userEntityArgumentCaptor;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        ticketRepository =  Mockito.mock(TicketRepository.class);
        eventRepository = Mockito.mock(EventRepository.class);
        userService = new UserService(userRepository, passwordEncoder,ticketRepository, eventRepository );
        userEntityArgumentCaptor = ArgumentCaptor.forClass(User.class);

    }

    ProjectionFactory factory = new SpelAwareProxyProjectionFactory();
    UserProjections userProjections = factory.createProjection(UserProjections.class);


    @Test
    void given_not_empty_list_when_fetch_the_list_then_users_list_should_be_returned() {
        //given
        List<UserProjections> list = new ArrayList<>();
        UserProjections user1 = Mockito.mock(UserProjections.class);
        UserProjections user2 = Mockito.mock(UserProjections.class);
        list.add(user1);
        list.add(user2);
        Page<UserProjections> page = new PageImpl<>(list,PageRequest.of(0,10),list.size());
        Mockito.when(userRepository.listOfUsers(any(Pageable.class))).thenReturn(page);
        Pageable pageable = PageRequest.of(0,10);
        //when
        var results = userService.getUserList(pageable);
        //then
        Assertions.assertEquals(page, results);
        Mockito.verify(userRepository, Mockito.times(1)).listOfUsers(pageable);
    }

    @Test
    void given_repo_with_not_existing_user_when_add_new_user_then_user_should_be_created() throws EmailExistsException {
        //given
        CreateUserRequest request = new CreateUserRequest("unitTest", "xxx4", "xyz@nq.pl");
        User response = User.builder()
                .userName(request.userName()).build();
        Mockito.when(userRepository.save(any())).thenReturn(response);
        //when
        var result = userService.createUser(request);
        //then
        Assertions.assertEquals(result.getUserName(), request.userName());


    }

    @Test
    void given_repo_with_existing_username_when_add_new_user_then_throws_exception() throws EmailExistsException {
        //given
        Mockito.when(userRepository.findByUserNameIgnoreCase(any())).thenReturn(Optional.of(USER));
        CreateUserRequest request = new CreateUserRequest("staticUser", "wrhge15", "qqq@q.pl");
        //when
        var exception = Assertions.assertThrows(DuplicateException.class, () -> userService.createUser(request));
        //then
        Mockito.verify(userRepository, Mockito.never()).save(any());
        var expectedMessage = String.format("This user %s already exist", request.userName());
        var actualMessage = exception.getMessage();
        Assertions.assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void given_user_with_existing_email_in_db_when_create_new_user_then_throws_exception() {
        //given
        Mockito.when(userRepository.findByEmail(any())).thenReturn(Optional.of(USER));
        CreateUserRequest request = new CreateUserRequest("newUserTest", "wrhge15", "yxq14@tr.pl");
        //when
        var exception = Assertions.assertThrows(EmailExistsException.class, ()-> userService.createUser(request));
        //then
        Mockito.verify(userRepository, Mockito.never()).save(any());
        var expectedMessage = String.format("There is an account with that email adress: " + request.email());
        var actualMessage = exception.getMessage();
        Assertions.assertEquals(expectedMessage,actualMessage);
    }

    @Test
    void given_existing_user_when_call_delete_method_with_user_id_then_user_and_all_related_info_should_be_deleted() {
        //given
        Event event = new Event(1L, "Test Event", "Location", 10.00,LocalDate.of(2024, 10, 2),
                LocalDate.of(2024, 10, 12),50, "Description", null, null);
        Set<Ticket> tickets = new HashSet<>();
        Ticket ticket = new Ticket();
        ticket.setEventEntity(event);
        User user = new User(2L, "userToDelete", "qwe", "vvv@v.pl", UserRole.PARTICIPANT, event, tickets);
        ticket.setUserEntity(user);
        tickets.add(ticket);
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(user)).thenReturn(Optional.empty());
        userService.delete(user.getId());
        //then
        Mockito.verify(userRepository, Mockito.times(1)).delete(user);
        assertThat(userRepository.findById(user.getId()).isEmpty());
        // Verify clearing user from event
        Mockito.verify(eventRepository, Mockito.times(1)).save(event);
        assertThat(event.getUserEntity()).isNull();
        //Verift clearing user from tickets
        for (Ticket tickett : tickets){
            Mockito.verify(ticketRepository, Mockito.times(1)).delete(tickett);
        }
        // Verify deleting tickets associated with user
        Mockito.verify(ticketRepository, Mockito.times(tickets.size())).delete(any());
    }

    @Test
    void given_not_existing_user_when_call_delete_method_with_not_existing_user_id_then_method_throws_exception() {
        //given
        User user = new User(2L, "userToDelete", "qwe", "vvv@v.pl", UserRole.PARTICIPANT, null, null);
        //when
        var exception = Assertions.assertThrows(EntityNotFoundException.class, ()->userService.delete(user.getId()));
        //then
        Mockito.verify(userRepository, Mockito.never()).delete(any());
        var expectedMessage =  String.format("User not found with id: " +  user.getId());
        var actualMessage = exception.getMessage();
        Assertions.assertEquals(expectedMessage,actualMessage);
    }

    @Test
    void given_existing_user_when_call_load_user_by_username_method_then_user_should_be_loaded(){
        //given
        String username = "testUser";
        String encodedPassword = "encodedPassword";
        UserRole role = UserRole.PARTICIPANT;
        User user = new User(1L, username, encodedPassword, "test@example.com", role, null, null);
        Mockito.when(userRepository.findByUserNameIgnoreCase(username)).thenReturn(Optional.of(user));
        //when
        UserDetails userDetails = userService.loadUserByUsername(username);
        //then
        Assertions.assertEquals(username, userDetails.getUsername());
        Assertions.assertEquals(encodedPassword, userDetails.getPassword());
        Assertions.assertEquals(1, userDetails.getAuthorities().size());
        Assertions.assertEquals(role.name(), userDetails.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void given_not_existing_user_when_call_load_user_by_username_method_then_method_throws_exception() {
        //given
        User user = new User(2L, "UsertoCheck", "qwe", "vvv@v.pl", UserRole.PARTICIPANT, null, null);
        Mockito.when(userRepository.findByUserNameIgnoreCase(user.getUserName())).thenReturn(Optional.empty());
        //when
        var exception = Assertions.assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(user.getUserName()));
        //then
        var expectedMessage =  String.format("User not found with username: " +  user.getUserName());
        var actualMessage = exception.getMessage();
        Assertions.assertEquals(expectedMessage,actualMessage);
    }



}