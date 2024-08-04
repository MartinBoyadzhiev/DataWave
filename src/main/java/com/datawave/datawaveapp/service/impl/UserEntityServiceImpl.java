package com.datawave.datawaveapp.service.impl;

import com.datawave.datawaveapp.config.securityConfig.JwtProvider;
import com.datawave.datawaveapp.model.dto.AuthResponseDTO;
import com.datawave.datawaveapp.model.dto.LoginDTO;
import com.datawave.datawaveapp.model.dto.SignUpDTO;
import com.datawave.datawaveapp.model.entity.UserEntity;
import com.datawave.datawaveapp.repository.mysqlRepositories.UserRepository;
import com.datawave.datawaveapp.service.UserEntityService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserEntityServiceImpl implements UserEntityService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final JwtProvider jwtProvider;


    public UserEntityServiceImpl(UserRepository userRepository,
                                 PasswordEncoder passwordEncoder,
                                 UserDetailsService userDetailsService,
                                 JwtProvider jwtProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
        this.jwtProvider = jwtProvider;
    }

    @Override
    public ResponseEntity<AuthResponseDTO> register(SignUpDTO user) throws Exception {
        String email = user.getEmail();
        String password = user.getPassword();
        String role = user.getRole();

        Optional<UserEntity> optionalUser = userRepository.findByEmail(email);
        //TODO: Setup unique constraint on email column in the database
        if (!optionalUser.isEmpty()) {
            throw new Exception("Email Is Already Used With Another Account");
        }

        UserEntity createdUser = new UserEntity();
        createdUser.setEmail(email);
        createdUser.setRole(role);
        createdUser.setPassword(passwordEncoder.encode(password));

        UserEntity savedUser = userRepository.save(createdUser);
        userRepository.save(savedUser);
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
        boolean isAdmin = optionalUser.isPresent() && "ROLE_ADMIN".equals(optionalUser.get().getRole());

        authResponse.setMessage("Login success");
        authResponse.setJwt(token);
        authResponse.setStatus(true);
        authResponse.setAdmin(isAdmin);

        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }

    private Authentication authenticate(String username, String password) {

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (userDetails == null) {
            throw new BadCredentialsException("Invalid username and password");
        }

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
