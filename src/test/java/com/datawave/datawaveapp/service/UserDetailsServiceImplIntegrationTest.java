package com.datawave.datawaveapp.service;

import com.datawave.datawaveapp.model.dto.AuthResponseDTO;
import com.datawave.datawaveapp.model.dto.SignUpDTO;
import com.datawave.datawaveapp.model.entity.RoleEntity;
import com.datawave.datawaveapp.model.entity.UserEntity;
import com.datawave.datawaveapp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;



@SpringBootTest
public class UserDetailsServiceImplIntegrationTest {

    @Autowired
    private com.datawave.datawaveapp.service.impl.UserEntityServiceImpl userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserEntityService userEntityService;

    @Test
    @Transactional
    @Rollback(true)
    void loadUserByUsernameSuccess() {
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail("test@example.com");
        userEntity.setPassword("password");

        userRepository.save(userEntity);

        UserDetails userDetails = userDetailsService.loadUserByUsername("test@example.com");

        assertEquals("test@example.com", userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
    }

    @Test
    void loadUserByUsernameNotFound() {

        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername("test@example.com"));
    }

    @Test
    void loadUserByUsernameNullEmail() {
        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(null));
    }

    @Test
    void loadUserByUsernameEmptyEmail() {
        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(""));
    }

    private ResponseEntity<AuthResponseDTO> createUser() throws Exception {
        SignUpDTO signUpDTO = new SignUpDTO("test@example.com", "password", "ROLE_USER");
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail("test@example.com");
        userEntity.setRoles(Set.of(new RoleEntity("USER")));
        userEntity.setPassword("encodedPassword");

        ResponseEntity<AuthResponseDTO> response = userEntityService.register(signUpDTO);
        return response;
    }
}
