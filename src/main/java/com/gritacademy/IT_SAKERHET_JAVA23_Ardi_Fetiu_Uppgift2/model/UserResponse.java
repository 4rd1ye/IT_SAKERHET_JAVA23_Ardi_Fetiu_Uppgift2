package com.gritacademy.IT_SAKERHET_JAVA23_Ardi_Fetiu_Uppgift2.model;

import java.util.List;

public class UserResponse {
    private String email;
    private List<String> roles;

    public UserResponse(String email, List<String> roles) {
        this.email = email;
        this.roles = roles;
    }

    public String getEmail() {
        return email;
    }

    public List<String> getRoles() {
        return roles;
    }
}
