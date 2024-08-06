package com.datawave.datawaveapp.service;

import com.datawave.datawaveapp.model.dto.AuthResponseDTO;
import com.datawave.datawaveapp.model.dto.LoginDTO;
import com.datawave.datawaveapp.model.dto.SignUpDTO;
import com.datawave.datawaveapp.model.entity.UserEntity;
import com.datawave.datawaveapp.service.exceptions.UserAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class UserEntityServiceImpl {

    @Autowired
    private UserEntityService userEntityService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @Transactional
    @Rollback(true)
    void registerSuccess() throws Exception {
        ResponseEntity<AuthResponseDTO> response = createUser();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getStatus());
    }



    @Test
    @Transactional
    @Rollback(true)
    void registerUserAlreadyExists() throws Exception {
        createUser();

        assertThrows(UserAlreadyExistsException.class, () -> createUser());
    }

    @Test
    @Transactional
    @Rollback(true)
    void getUserByEmailSuccess() throws Exception {
        createUser();

        UserEntity result = userEntityService.getUserByEmail("test@example.com");

        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void getUserByEmailNotFound() {

        assertThrows(BadCredentialsException.class, () -> userEntityService.getUserByEmail("test@example.com"));
    }

    @Test
    @Transactional
    @Rollback(true)
    void loginSuccess() throws Exception {
        LoginDTO loginDTO = new LoginDTO("test@example.com", "password");
        createUser();

        ResponseEntity<AuthResponseDTO> response = userEntityService.login(loginDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getStatus());
    }

    @Test
    @Transactional
    @Rollback(true)
    void loginInvalidPassword() throws Exception {
        LoginDTO loginDTO = new LoginDTO("test@example.com", "wrongPassword");
        createUser();

        assertThrows(BadCredentialsException.class, () -> userEntityService.login(loginDTO));
    }

    @Test
    @Transactional
    @Rollback(true)
    void loginUserNotFound() throws Exception {
        LoginDTO loginDTO = new LoginDTO("test@example.com", "passssssword");
        createUser();
        assertThrows(BadCredentialsException.class, () -> userEntityService.login(loginDTO));
    }

    private ResponseEntity<AuthResponseDTO> createUser() throws Exception {
        SignUpDTO signUpDTO = new SignUpDTO("test@example.com", "password", "ROLE_USER");
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail("test@example.com");
        userEntity.setRole("ROLE_USER");
        userEntity.setPassword("encodedPassword");

        ResponseEntity<AuthResponseDTO> response = userEntityService.register(signUpDTO);
        return response;
    }
}