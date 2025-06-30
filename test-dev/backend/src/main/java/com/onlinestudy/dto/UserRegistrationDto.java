package com.onlinestudy.dto;

import com.onlinestudy.domain.User;
import lombok.Data;

@Data
public class UserRegistrationDto {
    private String username;
    private String password;
    private String email;
    private User.Role role;
} 