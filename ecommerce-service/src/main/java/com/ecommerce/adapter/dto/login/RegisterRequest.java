package com.ecommerce.adapter.dto.login;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    private String username;
    private String password;
    private String role; // "ROLE_USER" ou "ROLE_ADMIN"

}