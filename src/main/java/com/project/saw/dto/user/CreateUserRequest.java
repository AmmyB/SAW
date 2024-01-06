package com.project.saw.dto.user;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;


public record CreateUserRequest(@Column(name = "user_name", unique = true)
                                @NotBlank(message = "Username is mandatory")
                                String userName,
                                @NotBlank(message = "Password is mandatory")
                                String password,
                                @Column(unique = true)
                                @Email(message = "Email should be valid")
                                String email) {
}
