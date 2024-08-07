package com.datawave.datawaveapp.service.impl;

import com.datawave.datawaveapp.config.securityConfig.JwtProvider;
import com.datawave.datawaveapp.model.dto.AuthResponseDTO;
import com.datawave.datawaveapp.model.dto.LoginDTO;
import com.datawave.datawaveapp.model.dto.SignUpDTO;
import com.datawave.datawaveapp.model.entity.RoleEntity;
import com.datawave.datawaveapp.model.entity.UserEntity;
import com.datawave.datawaveapp.repository.UserRepository;
import com.datawave.datawaveapp.service.RoleService;
import com.datawave.datawaveapp.service.UserEntityService;
import com.datawave.datawaveapp.service.exceptions.UserAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class UserEntityServiceImpl implements UserEntityService, UserDetailsService {
    private final RoleService roleService;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;


    public UserEntityServiceImpl(RoleService roleService, UserRepository userRepository,
                                 PasswordEncoder passwordEncoder,
                                 JwtProvider jwtProvider) {
        this.roleService = roleService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Optional<UserEntity> optionalUser = this.userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }
        UserEntity user = optionalUser.get();

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                getAuthority(user));
    }

    @Override
    public ResponseEntity<AuthResponseDTO> register(SignUpDTO user) {
        String email = user.getEmail();
        String password = user.getPassword();

        Optional<UserEntity> optionalUser = userRepository.findByEmail(email);

        if (!optionalUser.isEmpty()) {
            throw new UserAlreadyExistsException("User with " + email + " already exists");
        }

        RoleEntity userRole = this.roleService.findByName("USER");
        RoleEntity adminRole = this.roleService.findByName("ADMIN");

        UserEntity createdUser = new UserEntity();
        createdUser.setEmail(email);
        createdUser.setPassword(passwordEncoder.encode(password));
        createdUser.setRoles(new HashSet<>());

        if (this.userRepository.count() == 0) {
            createdUser.getRoles().add(userRole);
            createdUser.getRoles().add(adminRole);
        } else {
            createdUser.getRoles().add(userRole);
        }

        this.userRepository.save(createdUser);

        Authentication authentication = new UsernamePasswordAuthenticationToken(email, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = this.jwtProvider.generateToken(authentication);

        AuthResponseDTO authResponseDTO = new AuthResponseDTO();
        authResponseDTO.setJwt(token);
        authResponseDTO.setMessage("Register Success");
        authResponseDTO.setStatus(true);
        return new ResponseEntity<>(authResponseDTO, HttpStatus.OK);
    }

    @Override
    public UserEntity getUserByEmail(String email) {

        Optional<UserEntity> optionalUser = this.userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            throw new BadCredentialsException("User not found");
        }
        return optionalUser.get();
    }

    @Override
    public ResponseEntity<AuthResponseDTO> login(LoginDTO loginRequest) {
        String username = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        Authentication authentication = authenticate(username, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = this.jwtProvider.generateToken(authentication);
        AuthResponseDTO authResponse = new AuthResponseDTO();

        Optional<UserEntity> optionalUser = userRepository.findByEmail(username);
        if (optionalUser.isEmpty()) {
            throw new BadCredentialsException("User not found");
        }

        boolean isAdmin = optionalUser.get().getRoles()
                .stream()
                .anyMatch(role -> role.getName().equals("ADMIN"));

        authResponse.setMessage("Login success");
        authResponse.setJwt(token);
        authResponse.setStatus(true);
        authResponse.setAdmin(isAdmin);

        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }

    private Authentication authenticate(String username, String password) {

        UserDetails userDetails = this.loadUserByUsername(username);

        if (userDetails == null) {
            throw new BadCredentialsException("Invalid username and password");
        }

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    private Set<SimpleGrantedAuthority> getAuthority(UserEntity user) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        user.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
        });
        return authorities;
    }


}
