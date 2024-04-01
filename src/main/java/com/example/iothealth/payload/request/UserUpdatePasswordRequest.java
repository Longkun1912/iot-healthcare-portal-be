package com.example.iothealth.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserUpdatePasswordRequest {
    @JsonProperty
    private String email;

    @JsonProperty
    private String oldPassword;

    @JsonProperty
    private String newPassword;
}
