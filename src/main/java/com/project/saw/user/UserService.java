package com.project.saw.user;


import com.project.saw.dto.user.CreateUserRequest;
import com.project.saw.exception.DuplicateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

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

    public List<UserEntity> getUserList() {
        return userRepository.findAll().stream()
                .sorted(Comparator.comparing(UserEntity::getUserName))
                .toList();
    }

    public UserEntity createUser(CreateUserRequest request) {
        userRepository.findByUserNameIgnoreCase(request.userName())
                .ifPresent(UserEntity -> {
                    var error = String.format(DUPLICATE_USER_ERROR_MESSAGE, request.userName());
                    throw new DuplicateException(error);
                });

        UserEntity userEntity = UserEntity.builder()
                .userName(request.userName())
                .password(passwordEncoder.encode(request.password()))
                .email(request.email())
                .userRole(UserRole.valueOf("PARTICIPANT"))
                .build();

        return userRepository.save(userEntity);
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUserNameIgnoreCase(username)
                .map(user -> (
                    new User(user.getUserName(), user.getPassword(),List.of(new SimpleGrantedAuthority(user.getUserRole().name())))
                ))
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }
}