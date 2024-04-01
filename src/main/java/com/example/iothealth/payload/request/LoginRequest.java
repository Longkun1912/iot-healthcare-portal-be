package com.example.iothealth.payload.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class LoginRequest {
    @NotEmpty(message = "This field cannot be empty.")
    private String email;

    @NotEmpty(message = "This field cannot be empty.")
    private String password;
}
