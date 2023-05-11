package ru.ncti.backend.entiny.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import ru.ncti.backend.entiny.Schedule;
import ru.ncti.backend.entiny.User;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.List;

@Getter
@Setter
@Entity
public class Teacher extends User {

    @OneToMany(mappedBy = "teacher", fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Schedule> schedules;
}
