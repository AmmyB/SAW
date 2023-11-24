package com.project.saw.user;

import com.project.saw.exception.EmailExistsException;

import org.springframework.security.core.userdetails.*;

import com.project.saw.dto.user.CreateUserRequest;
import com.project.saw.dto.user.UserProjections;
import com.project.saw.exception.DuplicateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserService implements UserDetailsService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    private static final String DUPLICATE_USER_ERROR_MESSAGE = "This user %s already exist";


    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;

    }

    public List<UserProjections> getUserList() {
        return userRepository.listOfUsers(Sort.by("id"));
    }

    public UserEntity createUser(CreateUserRequest request) throws EmailExistsException {
        userRepository.findByUserNameIgnoreCase(request.userName())
                .ifPresent(UserEntity -> {
                    var error = String.format(DUPLICATE_USER_ERROR_MESSAGE, request.userName());
                    throw new DuplicateException(error);
                });
        if (emailExists(request.email())) {
            throw new EmailExistsException
                    ("There is an account with that email adress: " + request.email());
        }

        UserEntity userEntity = UserEntity.builder()
                .userName(request.userName())
                .password(passwordEncoder.encode(request.password()))
                .email(request.email())
                .userRole(UserRole.PARTICIPANT)
                .build();

        return userRepository.save(userEntity);
    }

    private boolean emailExists(final String email) {
        return userRepository.findByEmail(email) != null;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUserNameIgnoreCase(username)
                .map(user -> (
                        new User(user.getUserName(), user.getPassword(), List.of(new SimpleGrantedAuthority(user.getUserRole().name())))
                ))
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }

    public void delete(Long userId) {
        userRepository.deleteById(userId);
    }
}
