package ru.ncti.backend.service;

import lombok.extern.log4j.Log4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ncti.backend.entiny.Group;
import ru.ncti.backend.entiny.Sample;
import ru.ncti.backend.entiny.users.Student;
import ru.ncti.backend.repository.SampleRepository;

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

    private final SampleRepository sampleRepository;

    public StudentService(SampleRepository sampleRepository) {
        this.sampleRepository = sampleRepository;
    }

    @Transactional(readOnly = true)
    public Student getProfile() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return (Student) auth.getPrincipal();
    }

    @Transactional(readOnly = true)
    public Map<String, Set<Sample>> getSchedule() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        Student student = (Student) auth.getPrincipal();

        Map<String, Set<Sample>> map = new HashMap<>();

        Set<Sample> currSample = getTypeSchedule(student.getGroup());

        for (Sample s : currSample) {
            map.computeIfAbsent(s.getDay(), k -> new HashSet<>()).add(s);
        }

        map.forEach((key, value) -> {
            Set<Sample> sortedSet = value.stream()
                    .sorted(Comparator.comparingInt(Sample::getNumberPair))
                    .collect(Collectors.toCollection(LinkedHashSet::new));
            map.put(key, sortedSet);
        });

        return map;
    }

    private Set<Sample> getTypeSchedule(Group group) {
        List<Sample> sample = sampleRepository.findAllByGroup(group);
        String currentWeekType = getCurrentWeekType();
        return sample.stream()
                .filter(s -> s.getParity().equals("0") || s.getParity().equals(currentWeekType))
                .collect(Collectors.toSet());
    }

    private String getCurrentWeekType() {
        LocalDate currentDate = LocalDate.now();
        int currentWeekNumber = currentDate.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
        return currentWeekNumber % 2 == 0 ? "2" : "1";
    }

}
