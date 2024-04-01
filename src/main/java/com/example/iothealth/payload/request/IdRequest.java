package com.example.iothealth.payload.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class IdRequest {
    @NotEmpty(message = "This field cannot be empty.")
    private String id;
}
