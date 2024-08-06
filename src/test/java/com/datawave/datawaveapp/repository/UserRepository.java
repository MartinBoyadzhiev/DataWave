package com.datawave.datawaveapp.repository;

import com.datawave.datawaveapp.model.entity.UserEntity;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class UserRepository {

    private com.datawave.datawaveapp.repository.mysqlRepositories.UserRepository userRepository = mock(com.datawave.datawaveapp.repository.mysqlRepositories.UserRepository.class);

    @Test
    void findByEmail_ReturnsUserEntity_WhenEmailExists() {
        UserEntity user = new UserEntity();
        when(userRepository.findByEmail("existingEmail@example.com")).thenReturn(Optional.of(user));
        Optional<UserEntity> result = userRepository.findByEmail("existingEmail@example.com");
        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    void findByEmail_ReturnsEmptyOptional_WhenEmailDoesNotExist() {
        when(userRepository.findByEmail("nonExistentEmail@example.com")).thenReturn(Optional.empty());
        Optional<UserEntity> result = userRepository.findByEmail("nonExistentEmail@example.com");
        assertFalse(result.isPresent());
    }
}
