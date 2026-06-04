package com.example.ecommerce.security.response;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class UserInfoResponse {
    private Long id;
    private String jwtToken;
    private String username;
    private String email;
    private List<String> roles;

    public UserInfoResponse(Long id, String username, List<String> roles, String email, String jwtToken) {
        this.id = id;
        this.username = username;
        this.roles = roles;
        this.email = email;
        this.jwtToken = jwtToken;
    }

    public UserInfoResponse(Long id, String username, List<String> roles) {
        this.id = id;
        this.username = username;
        this.roles = roles;
    }
}