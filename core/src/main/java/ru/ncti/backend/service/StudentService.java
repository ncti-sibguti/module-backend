package ru.ncti.backend.service;

import javassist.NotFoundException;
import lombok.extern.log4j.Log4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ncti.backend.entiny.Group;
import ru.ncti.backend.entiny.Schedule;
import ru.ncti.backend.entiny.users.Student;
import ru.ncti.backend.repository.ScheduleRepository;

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
public class StudentService {

    private final ScheduleRepository scheduleRepository;

    public StudentService(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    @Transactional(readOnly = true)
    public Student getProfile() throws NotFoundException {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return (Student) auth.getPrincipal();
    }

    @Transactional(readOnly = true)
    public Map<String, Set<Schedule>> getSchedule() throws NotFoundException {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        Student student = (Student) auth.getPrincipal();

        Map<String, Set<Schedule>> map = new HashMap<>();

        Set<Schedule> currSchedule = getTypeSchedule(student.getGroup());

        for (Schedule s : currSchedule) {
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

    private Set<Schedule> getTypeSchedule(Group group) {
        List<Schedule> schedule = scheduleRepository.findAllByGroup(group);
        int currentWeekType = getCurrentWeekType();
        return schedule.stream()
                .filter(s -> s.getType() == 0 || s.getType() == currentWeekType)
                .collect(Collectors.toSet());
    }

    private int getCurrentWeekType() {
        LocalDate currentDate = LocalDate.now();
        int currentWeekNumber = currentDate.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
        return currentWeekNumber % 2 == 0 ? 2 : 1;
    }

}
