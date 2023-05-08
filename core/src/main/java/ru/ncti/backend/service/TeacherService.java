package ru.ncti.backend.service;

import javassist.NotFoundException;
import lombok.extern.log4j.Log4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ncti.backend.entiny.Schedule;
import ru.ncti.backend.entiny.Teacher;
import ru.ncti.backend.entiny.enums.WeekType;
import ru.ncti.backend.repository.ScheduleRepository;
import ru.ncti.backend.repository.TeacherRepository;
import ru.ncti.backend.repository.UserRepository;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Log4j
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    public TeacherService(TeacherRepository teacherRepository,
                          ScheduleRepository scheduleRepository,
                          UserRepository userRepository) {
        this.teacherRepository = teacherRepository;
        this.scheduleRepository = scheduleRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Teacher getProfile() throws NotFoundException {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return (Teacher) auth.getPrincipal();
    }

    @Transactional(readOnly = true)
    public Map<String, Set<Schedule>> getSchedule() throws NotFoundException {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        Teacher teacher = (Teacher) auth.getPrincipal();

        Map<String, Set<Schedule>> map = new HashMap<>();

        for (Schedule s : getTypeSchedule(teacher)) {
            map.computeIfAbsent(s.getDay(), k -> new HashSet<>()).add(s);
        }

        map.forEach((key, value) -> {
            Set<Schedule> sortedSet = value.stream()
                    .sorted(Comparator.comparingInt(Schedule::getNumberPair))
                    .collect(Collectors.toCollection(LinkedHashSet::new));
            map.put(key, sortedSet);
        });

        return map;
    }

    private Set<Schedule> getTypeSchedule(Teacher t) {
        List<Schedule> schedule = scheduleRepository.findAllByTeacher(t);
        WeekType currentWeekType = getCurrentWeekType();
        return schedule.stream()
                .filter(s -> s.getType() == WeekType.CONST || s.getType() == currentWeekType)
                .collect(Collectors.toSet());
    }

    private WeekType getCurrentWeekType() {
        LocalDate currentDate = LocalDate.now();
        int currentWeekNumber = currentDate.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
        return currentWeekNumber % 2 == 0 ? WeekType.EVEN : WeekType.ODD;
    }

}
