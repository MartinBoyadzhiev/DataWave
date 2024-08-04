package com.datawave.datawaveapp.service;

import com.datawave.datawaveapp.model.dto.AuthResponseDTO;
import com.datawave.datawaveapp.model.dto.LoginDTO;
import com.datawave.datawaveapp.model.dto.SignUpDTO;
import com.datawave.datawaveapp.model.entity.UserEntity;
import org.springframework.http.ResponseEntity;

public interface UserEntityService {
    ResponseEntity<AuthResponseDTO> login(LoginDTO loginRequest);
    ResponseEntity<AuthResponseDTO> register(SignUpDTO user) throws Exception;

    UserEntity getUserByEmail(String email);
}
