package ru.ncti.backend.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ncti.backend.entiny.Role;
import ru.ncti.backend.entiny.User;
import ru.ncti.backend.repository.RoleRepository;
import ru.ncti.backend.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        return userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User " + usernameOrEmail + " not found"));
    }

    @Transactional(readOnly = true)
    public List<User> getUsers(String type) {
        if (type != null) {
            switch (type) {
                case "student" -> {
                    Optional<Role> role = roleRepository.findByName("ROLE_STUDENT");
                    if (role.isPresent())
                        return userRepository.findAllByRolesIn(Set.of(role.get()));
                    else
                        return Collections.emptyList();
                }
                case "teacher" -> {
                    Optional<Role> role = roleRepository.findByName("ROLE_TEACHER");
                    if (role.isPresent())
                        return userRepository.findAllByRolesIn(Set.of(role.get()));
                    else
                        return Collections.emptyList();
                }
            }

        }

        return userRepository.findAll();
    }
}
