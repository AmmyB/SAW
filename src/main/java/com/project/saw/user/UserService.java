package com.project.saw.user;

import com.project.saw.exception.EmailExistsException;

import com.project.saw.exception.ExceptionMessage;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.userdetails.*;

import com.project.saw.dto.user.CreateUserRequest;
import com.project.saw.dto.user.UserProjections;
import com.project.saw.exception.DuplicateException;
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


    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;

    }

    public List<UserProjections> getUserList() {
        return userRepository.listOfUsers(Sort.by("id"));
    }

    public User createUser(CreateUserRequest request) throws EmailExistsException {
        userRepository.findByUserNameIgnoreCase(request.userName())
                .ifPresent(UserEntity -> {
                    var error = String.format(ExceptionMessage.DUPLICATE_USER_ERROR_MESSAGE, request.userName());
                    throw new DuplicateException(error);
                });
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new EmailExistsException
                    (ExceptionMessage.EMAIL_EXISTS_ERROR_MESSAGE + request.email());
        }

        User userEntity = User.builder()
                .userName(request.userName())
                .password(passwordEncoder.encode(request.password()))
                .email(request.email())
                .userRole(UserRole.PARTICIPANT)
                .build();

        return userRepository.save(userEntity);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUserNameIgnoreCase(username)
                .map(user -> (
                        new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPassword(), List.of(new SimpleGrantedAuthority(user.getUserRole().name())))
                ))
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }

    public void delete(Long userId) {
       User userToDelete = userRepository.findById(userId)
               .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.USER_NOT_FOUND_EXCEPTION_MESSAGE + userId));
       userRepository.delete(userToDelete);

    }
}
