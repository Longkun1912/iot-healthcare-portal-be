package com.example.iothealth.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.UUID;

@Data
public class AssignDeviceRequest {
    @JsonProperty
    private String owner;

    @JsonProperty
    private UUID device_id;
}
