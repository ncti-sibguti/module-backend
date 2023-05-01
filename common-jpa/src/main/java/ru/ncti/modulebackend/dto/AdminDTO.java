package ru.ncti.modulebackend.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class AdminDTO {
    @NotBlank(message = "username is mandatory")
    @Min(value = 3, message = "Username should be at least 6 characters long")
    private String username;
    @NotBlank(message = "password is mandatory")
    private String password;
}

