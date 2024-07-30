package com.datawave.datawaveapp.web;

import com.datawave.datawaveapp.model.dto.AuthResponseDTO;
import com.datawave.datawaveapp.model.dto.LoginDTO;
import com.datawave.datawaveapp.model.dto.SignUpDTO;
import com.datawave.datawaveapp.service.UserEntityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class UserController {

    private final UserEntityService userService;

    public UserController(UserEntityService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponseDTO> createUserHandler(@RequestBody SignUpDTO user) throws Exception {
        return userService.doRegister(user);
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponseDTO> signIn(@RequestBody LoginDTO loginRequest) {
        return userService.doLogin(loginRequest);
    }
}