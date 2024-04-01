package com.example.iothealth.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class SignupRequest {
    @JsonProperty
    private String username;

    @JsonProperty
    private MultipartFile avatar;

    @JsonProperty
    private String email;

    @JsonProperty
    private String mobile;

    @JsonProperty
    private String gender;

    @JsonProperty
    private String password;

    @JsonProperty
    private String confirm_password;
}
