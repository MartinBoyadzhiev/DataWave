package com.datawave.datawaveapp.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class SignUpDTO {
    @Email
    @NotEmpty
    private String email;
    @Size(min = 6)
    @NotEmpty
    private String password;

    public SignUpDTO(String email, String password, String role) {
        this.email = email;
        this.password = password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }


}
