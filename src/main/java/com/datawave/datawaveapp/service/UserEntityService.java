package com.datawave.datawaveapp.service;

import com.datawave.datawaveapp.model.dto.AuthResponseDTO;
import com.datawave.datawaveapp.model.dto.LoginDTO;
import com.datawave.datawaveapp.model.dto.SignUpDTO;
import org.springframework.http.ResponseEntity;

public interface UserEntityService {
    ResponseEntity<AuthResponseDTO> login(LoginDTO loginRequest);
    ResponseEntity<AuthResponseDTO> register(SignUpDTO user) throws Exception;
}
