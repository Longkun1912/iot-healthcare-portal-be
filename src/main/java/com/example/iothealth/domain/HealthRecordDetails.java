package com.example.iothealth.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class HealthRecordDetails {
    @JsonProperty
    private UUID id;

    @JsonProperty
    private int heart_rate;

    @JsonProperty
    private int blood_pressure;

    @JsonProperty
    private float temperature;

    @JsonProperty
    private LocalDateTime last_updated;
}
