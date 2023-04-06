package ru.ncti.modulebackend.service;

import org.springframework.stereotype.Service;
import ru.ncti.modulebackend.entiny.Role;
import ru.ncti.modulebackend.entiny.User;
import ru.ncti.modulebackend.repository.RoleRepository;
import ru.ncti.modulebackend.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public List<User> getUsers(String type) {

        if (type != null) {

            switch (type) {
                case "student": {
                    Optional<Role> role = roleRepository.findByName("ROLE_STUDENT");
                    return userRepository.findAllByRolesIn(Set.of(role.get()));
                }
                case "teacher": {
                    Optional<Role> role = roleRepository.findByName("ROLE_TEACHER");
                    return userRepository.findAllByRolesIn(Set.of(role.get()));
                }
            }

        }

        return userRepository.findAll();
    }

}
