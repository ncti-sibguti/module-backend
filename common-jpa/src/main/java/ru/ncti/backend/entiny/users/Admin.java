package ru.ncti.backend.entiny.users;

import lombok.Getter;
import lombok.Setter;
import ru.ncti.backend.entiny.User;

import javax.persistence.Entity;

@Getter
@Setter
@Entity
public class Admin extends User {
}
