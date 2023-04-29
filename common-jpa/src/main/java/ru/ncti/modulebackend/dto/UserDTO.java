package ru.ncti.modulebackend.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Set;


@Getter
@Setter
public class UserDTO {

    @NotBlank(message = "firstname is mandatory")
    private String firstname;
    @NotBlank(message = "lastname is mandatory")
    private String lastname;
    @NotBlank(message = "surname is mandatory")
    private String surname;
    @Email(message = "Email is mandatory")
    private String email;
    private String username;
    @NotBlank(message = "password is mandatory")
    private String password;
    private Set<String> roles;

}
