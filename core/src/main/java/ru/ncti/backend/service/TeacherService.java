package ru.ncti.backend.service;

import javassist.NotFoundException;
import lombok.extern.log4j.Log4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ncti.backend.dto.TeacherScheduleDTO;
import ru.ncti.backend.entiny.Schedule;
import ru.ncti.backend.entiny.Teacher;
import ru.ncti.backend.entiny.enums.WeekType;
import ru.ncti.backend.repository.ScheduleRepository;
import ru.ncti.backend.repository.TeacherRepository;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Log4j
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final ScheduleRepository scheduleRepository;

    public TeacherService(TeacherRepository teacherRepository,
                          ScheduleRepository scheduleRepository) {
        this.teacherRepository = teacherRepository;
        this.scheduleRepository = scheduleRepository;
    }

    @Transactional(readOnly = true)
    public Teacher getProfile() throws NotFoundException {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return (Teacher) auth.getPrincipal();
    }

    @Transactional(readOnly = true)
    public Map<String, Set<TeacherScheduleDTO>> getSchedule() throws NotFoundException {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        Teacher teacher = (Teacher) auth.getPrincipal();
        return makeSchedule(teacher.getSchedules());
    }

    private Map<String, Set<TeacherScheduleDTO>> makeSchedule(List<Schedule> list) {
        Map<String, Set<TeacherScheduleDTO>> map = new HashMap<>();

        for (Schedule s : getTypeSchedule(list)) {
            String key = s.getDay();
            TeacherScheduleDTO dto = TeacherScheduleDTO.builder()
                    .classroom(s.getClassroom())
                    .groups(List.of(s.getGroup().getName()))
                    .numberPair(s.getNumberPair())
                    .subject(s.getSubject())
                    .build();

// Проверяем наличие предмета с таким же номером пары и названием предмета
            Optional<TeacherScheduleDTO> found = map.getOrDefault(key, Collections.emptySet())
                    .stream()
                    .filter(scheduleDTO ->
                            scheduleDTO.getNumberPair().equals(dto.getNumberPair()) &&
                                    scheduleDTO.getSubject().equals(dto.getSubject()) &&
                                    scheduleDTO.getClassroom().equals(dto.getClassroom())
                    )
                    .findFirst();

// Если предмет найден, объединяем группы
            if (found.isPresent()) {
                TeacherScheduleDTO existing = found.get();
                Set<String> groups = new HashSet<>(existing.getGroups());
                groups.addAll(dto.getGroups());
                existing.setGroups(new ArrayList<>(groups));
            } else {
                map.computeIfAbsent(key, k -> new HashSet<>()).add(dto);
            }
        }
        sortedMap(map);
        return map;
    }

    private void sortedMap(Map<String, Set<TeacherScheduleDTO>> map) {
        map.forEach((key, value) -> {
            Set<TeacherScheduleDTO> sortedSet = value.stream()
                    .sorted(Comparator.comparingInt(TeacherScheduleDTO::getNumberPair))
                    .collect(Collectors.toCollection(LinkedHashSet::new));
            map.put(key, sortedSet);
        });
    }

    private Set<Schedule> getTypeSchedule(List<Schedule> list) {
        WeekType currentWeekType = getCurrentWeekType();
        return list.stream()
                .filter(s -> s.getType() == WeekType.CONST || s.getType() == currentWeekType)
                .collect(Collectors.toSet());
    }

    private WeekType getCurrentWeekType() {
        LocalDate currentDate = LocalDate.now();
        int currentWeekNumber = currentDate.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
        return currentWeekNumber % 2 == 0 ? WeekType.EVEN : WeekType.ODD;
    }

}