package com.datawave.datawaveapp.controller;

import com.datawave.datawaveapp.model.entity.UserEntity;
import com.datawave.datawaveapp.repository.mysqlRepositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserController {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @Transactional
    @Rollback(true)
    void testRegistration() throws Exception {
        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"email\": \"newww@nov.com\",\n" +
                        "    \"password\": \"testtest\",\n" +
                        "    \"role\": \"ROLE_USER\"\n" +
                        "}"))
                .andExpect(status().isOk());

        Optional<UserEntity> user = userRepository.findByEmail("newww@nov.com");
        Assertions.assertTrue(user.isPresent());

        UserEntity userEntity = user.get();
        Assertions.assertEquals("newww@nov.com", userEntity.getEmail());
        Assertions.assertTrue(passwordEncoder.matches("testtest", userEntity.getPassword()));
        Assertions.assertEquals("ROLE_USER", userEntity.getRole());
    }
}
