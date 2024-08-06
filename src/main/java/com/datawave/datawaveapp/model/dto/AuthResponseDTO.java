package com.datawave.datawaveapp.model.dto;

public class AuthResponseDTO {
    private String jwt;
    private String message;
    private boolean isAdmin;
    private boolean status;

    public AuthResponseDTO() {
    }

    public AuthResponseDTO(String jwt, String message, boolean isAdmin, boolean status) {
        this.jwt = jwt;
        this.message = message;
        this.isAdmin = isAdmin;
        this.status = status;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }


    public void setMessage(String message) {
        this.message = message;
    }


    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public boolean isStatus() {
        return status;
    }
}
