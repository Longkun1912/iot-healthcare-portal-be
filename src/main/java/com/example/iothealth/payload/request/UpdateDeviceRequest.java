package com.example.iothealth.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.UUID;

@Data
public class UpdateDeviceRequest {
    @JsonProperty
    private UUID id;

    @JsonProperty
    private String name;

    @JsonProperty
    private String image;

    @JsonProperty
    private String application_type;

    @JsonProperty
    private String status;

    @JsonProperty
    private String description;
}
