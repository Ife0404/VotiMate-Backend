package com.codewiz.signupdemo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    @NotBlank(message = "Matric number is required")
    @JsonProperty("matricNumber")
    private String matricNumber;

    @JsonProperty("password")
    @NotBlank(message = "Password is required")
    private String password;

}
