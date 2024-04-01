package com.example.iothealth.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
@Data
public class AddEditUserRequest {
    @JsonProperty
    private String id;

    @JsonProperty
    private String username;

    @JsonProperty
    private MultipartFile avatar;

    @JsonProperty
    private String email;

    @JsonProperty
    private String password;

    @JsonProperty
    private String mobile;

    @JsonProperty
    private String gender;

    @JsonProperty
    private List<String> roles;

    @JsonProperty
    private String organisation;
}
