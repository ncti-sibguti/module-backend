package ru.ncti.backend.service;

import lombok.extern.log4j.Log4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ncti.backend.dto.TeacherScheduleDTO;
import ru.ncti.backend.entiny.Sample;
import ru.ncti.backend.entiny.users.Teacher;
import ru.ncti.backend.repository.SampleRepository;
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
    private final SampleRepository sampleRepository;

    public TeacherService(TeacherRepository teacherRepository,
                          SampleRepository sampleRepository) {
        this.teacherRepository = teacherRepository;
        this.sampleRepository = sampleRepository;
    }

    @Transactional(readOnly = true)
    public Teacher getProfile() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return (Teacher) auth.getPrincipal();
    }

    @Transactional(readOnly = true)
    public Map<String, Set<TeacherScheduleDTO>> getSchedule() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        Teacher teacher = (Teacher) auth.getPrincipal();
        return makeSchedule(teacher.getSamples());
    }

    private Map<String, Set<TeacherScheduleDTO>> makeSchedule(List<Sample> list) {
        Map<String, Set<TeacherScheduleDTO>> map = new HashMap<>();

        for (Sample s : getTypeSchedule(list)) {
            log.info(s.getSubject().getName());
            String key = s.getDay();
            TeacherScheduleDTO dto = TeacherScheduleDTO.builder()
                    .classroom(s.getClassroom())
                    .groups(List.of(s.getGroup().getName()))
                    .numberPair(s.getNumberPair())
                    .subject(s.getSubject().getName())
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

    private Set<Sample> getTypeSchedule(List<Sample> list) {
        String currentWeekType = getCurrentWeekType();
        return list.stream()
                .filter(s -> s.getParity().equals("0") || s.getParity().equals(currentWeekType))
                .collect(Collectors.toSet());
    }

    private String getCurrentWeekType() {
        LocalDate currentDate = LocalDate.now();
        int currentWeekNumber = currentDate.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
        return currentWeekNumber % 2 == 0 ? "2" : "1";
    }

}