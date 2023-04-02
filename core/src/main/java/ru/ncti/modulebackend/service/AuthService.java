package ru.ncti.modulebackend.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.ncti.modulebackend.dto.UserDTO;
import ru.ncti.modulebackend.entiny.Role;
import ru.ncti.modulebackend.entiny.Student;
import ru.ncti.modulebackend.entiny.User;
import ru.ncti.modulebackend.repository.RoleRepository;
import ru.ncti.modulebackend.repository.UserRepository;
import ru.ncti.modulebackend.security.AuthDTO;
import ru.ncti.modulebackend.security.JwtTokenUtil;
import ru.ncti.modulebackend.security.UserDetailsImpl;
import ru.ncti.modulebackend.security.UserDetailsServiceImpl;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;

    public AuthService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       JwtTokenUtil jwtTokenUtil,
                       ModelMapper modelMapper,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       UserDetailsServiceImpl userDetailsService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.jwtTokenUtil = jwtTokenUtil;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    public User register(UserDTO dto) {
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new UsernameNotFoundException("User " + dto.getUsername() + " already exist");
        }

        User candidate = convert(dto, Student.class);

        Set<Role> roles = new HashSet<>(dto.getRoles().size());
        for (String role : dto.getRoles()) {
            System.out.println(roleRepository.findByName(role).get());
            if (roleRepository.findByName(role).isPresent())
                roles.add(roleRepository.findByName(role).get());
        }
        System.out.println(roles);
        candidate.setRoles(roles);
        candidate.setPassword(passwordEncoder.encode(dto.getPassword()));
        userRepository.save(candidate);
        return candidate;
    }

    public Map<String, String> login(AuthDTO dto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        var userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(dto.getUsername());

        String token = jwtTokenUtil.generateToken(userDetails);

        return Map.of("token", token);
    }

    private <S, D> D convert(S source, Class<D> dClass) {
        return modelMapper.map(source, dClass);
    }

}
