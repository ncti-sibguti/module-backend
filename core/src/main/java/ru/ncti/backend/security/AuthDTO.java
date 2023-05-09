package ru.ncti.backend.security;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthDTO {
    private String username;
    private String password;
}
