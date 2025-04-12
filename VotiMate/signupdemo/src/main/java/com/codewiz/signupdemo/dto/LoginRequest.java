package com.codewiz.signupdemo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    @NotBlank(message = "Matric number is required")
    private String matricNumber;

    @NotBlank(message = "Password is required")
    private String password;

}