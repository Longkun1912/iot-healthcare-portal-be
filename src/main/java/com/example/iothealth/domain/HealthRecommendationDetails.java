package com.example.iothealth.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class HealthRecommendationDetails {
    @JsonProperty
    private int id;

    @JsonProperty
    private String name;

    @JsonProperty
    private String heart_rate_impact;

    @JsonProperty
    private String blood_pressure_impact;

    @JsonProperty
    private String temperature_impact;

    @JsonProperty
    private String description;

    @JsonProperty
    private String guide_link;
}
