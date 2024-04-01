package com.example.iothealth.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UserProfileRequest {
    @JsonProperty
    private String username;

    @JsonProperty
    private String email;

    @JsonProperty
    private MultipartFile avatar;

    @JsonProperty
    private String mobile;

    @JsonProperty
    private String gender;
}
