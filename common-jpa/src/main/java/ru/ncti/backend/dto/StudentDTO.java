package ru.ncti.backend.dto;


import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class StudentDTO {
    @NotBlank(message = "firstname is mandatory")
    private String firstname;
    @NotBlank(message = "lastname is mandatory")
    private String lastname;
    @NotBlank(message = "surname is mandatory")
    private String surname;
    @Email(message = "Email is mandatory")
    private String email;
    @NotBlank(message = "password is mandatory")
    private String password;
    private String group;
}
