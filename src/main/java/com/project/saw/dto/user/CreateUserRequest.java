package com.project.saw.dto.user;

public record CreateUserRequest(String userName,
                                String password,
                                String email) {
}
