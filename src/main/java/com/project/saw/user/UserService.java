package com.project.saw.user;


import com.project.saw.event.Event;
import com.project.saw.event.EventRepository;
import com.project.saw.exception.DuplicateException;
import com.project.saw.exception.EmailExistsException;
import com.project.saw.exception.ExceptionMessage;
import com.project.saw.ticket.Ticket;
import com.project.saw.ticket.TicketRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.*;

import com.project.saw.dto.user.CreateUserRequest;
import com.project.saw.dto.user.UserProjections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class UserService implements UserDetailsService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TicketRepository ticketRepository;
    private final EventRepository eventRepository;


    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, TicketRepository ticketRepository, EventRepository eventRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.ticketRepository = ticketRepository;
        this.eventRepository = eventRepository;

    }

    public Page<UserProjections> getUserList(Pageable pageable) {
        return userRepository.listOfUsers(pageable);
    }

    public User createUser(CreateUserRequest request) throws EmailExistsException {
        userRepository.findByUserNameIgnoreCase(request.userName())
                .ifPresent(UserEntity -> {
                    var error = String.format(ExceptionMessage.DUPLICATE_USER_ERROR_MESSAGE, request.userName());
                    throw new DuplicateException(error);
                });
        if (emailExists(request.email())) {
            throw new EmailExistsException
                    ("There is an account with that email adress: " + request.email());
        }

        User userEntity = User.builder()
                .userName(request.userName())
                .password(passwordEncoder.encode(request.password()))
                .email(request.email())
                .userRole(UserRole.PARTICIPANT)
                .build();
        userRepository.save(userEntity);
        return userEntity;
    }

    private boolean emailExists(final String email) {
        return userRepository.findByEmail(email).isPresent();
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUserNameIgnoreCase(username)
                .map(user -> (
                        new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPassword(), List.of(new SimpleGrantedAuthority(user.getUserRole().name())))
                ))
                .orElseThrow(() -> new UsernameNotFoundException(ExceptionMessage.USERNAME_NOT_FOUND_EXCEPTION_MESSAGE + username));
    }

    @Transactional
    public void delete(Long userId) {
       User userToDelete = userRepository.findById(userId)
               .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.USER_NOT_FOUND_EXCEPTION_MESSAGE + userId));

       Event eventEntity = userToDelete.getEventEntity();
       if (eventEntity != null){
           eventEntity.setUserEntity(null);
           eventRepository.save(eventEntity);
       }

       for (Ticket ticket: userToDelete.getTicketEntities()){
           ticket.setUserEntity(null);
           ticketRepository.delete(ticket);
       }
       userRepository.delete(userToDelete);
    }
}
