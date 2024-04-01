package com.example.iothealth.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class UserInfoDetails {
    @JsonProperty
    private UUID id;

    @JsonProperty
    private String username;

    @JsonProperty
    private String avatar;

    @JsonProperty
    private String email;

    @JsonProperty
    private String mobile;

    @JsonProperty
    private String gender;

    @JsonProperty
    private String last_updated;

    @JsonProperty
    private Set<String> roles;

    @JsonProperty
    private String organisation;
}
