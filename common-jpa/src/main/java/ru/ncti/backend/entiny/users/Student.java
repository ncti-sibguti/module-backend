package ru.ncti.backend.entiny.users;

import lombok.Getter;
import lombok.Setter;
import ru.ncti.backend.entiny.Group;
import ru.ncti.backend.entiny.User;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Getter
@Setter
@Entity
public class Student extends User {

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "group_id")
    private Group group;
}
