package com.example.iothealth.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class RolesInforDetails {
    @JsonProperty
    private int id;
    @JsonProperty
    private String roleName;

    @JsonProperty
    private List<UserInfoDetails> users;
}
